package com.github.windymelt.tfs.application

import akka.actor.testkit.typed.scaladsl.ActorTestKit
import akka.actor.typed.ActorRef
import akka.actor.typed.ActorSystem
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.scaladsl.LoggerOps
import akka.stream.*
import akka.stream.scaladsl.Sink
import com.github.windymelt.tfs.TestHelper
import com.github.windymelt.tfs.UnitSpec
import com.github.windymelt.tfs.domain.Content
import com.github.windymelt.tfs.infrastructure.InmemoryComponent
import com.github.windymelt.tfs.infrastructure.InmemoryContentRepositoryComponent
import com.typesafe.config.ConfigFactory
import org.scalatest.BeforeAndAfter
import org.scalatest.BeforeAndAfterAll

import scala.concurrent.Future
import scala.language.postfixOps

import concurrent.duration.DurationInt
import com.github.windymelt.tfs.domain.ContentRepositoryCtx
import com.github.windymelt.tfs.AsyncUnitSpec

object AddCountActor:
  enum Msg:
    case Add(count: Int)
    case GetResult(replyTo: ActorRef[ResultResponse])

  case class ResultResponse(n: Int)

  def apply(currentCount: Int = 0): Behavior[Msg] = Behaviors.receive {
    (ctx, msg) =>
      msg match
        case Msg.Add(cnt) =>
          apply(currentCount + cnt)
        case Msg.GetResult(to) => {
          ctx.log.info(s"resulting $currentCount")
          to ! ResultResponse(currentCount)
          Behaviors.stopped
        }
  }

class LoadContentApplicationComponentSpec
    extends AsyncUnitSpec
    with BeforeAndAfter
    with BeforeAndAfterAll
    with InmemoryComponent
    with InmemoryContentRepositoryComponent
    with com.github.windymelt.tfs.infrastructure.ActorSystemComponent
    with LoadContentFromContentRepositoryApplicationComponent:

  ConfigFactory.load()

  val testKit = ActorTestKit()
  import akka.actor.testkit.typed.scaladsl.ActorTestKit

  val inmemorySystem = new InmemorySystem()
  before {
    inmemorySystem.reset()
  }

  override def afterAll() = testKit.shutdownTestKit()

  "pastContentAsSource" should "load all contents" in {
    inmemorySystem.originalContents =
      collection.mutable.Seq(1 to 123*) map TestHelper.genDummyContent

    val source =
      loadContentFromContentRepositoryApplication.pastContentAsSource(
        InmemoryContentRepository.initialContextForRetrievePastContent
      )
    val counterActor = testKit.spawn(AddCountActor(0), "counter")
    val probe = testKit.createTestProbe[AddCountActor.ResultResponse]()
    val f = (cs: (Seq[Content], ContentRepositoryCtx)) =>
      counterActor ! AddCountActor.Msg.Add(cs._1.size)
      cs
    val g = source.map(f)
    // takeLast(2)することで、枯渇する1つ前のカーソルを取得できる。次回はこれを使って処理を再開できる
    val result = g.runWith(Sink.takeLast(2))
    result.map { res =>
      counterActor ! AddCountActor.Msg.GetResult(probe.ref)
      probe.expectMessage(AddCountActor.ResultResponse(123))
      res.head._2.cursor shouldBe 120
    }(ec)

  }
