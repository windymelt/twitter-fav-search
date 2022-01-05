package com.github.windymelt.tfs

import com.github.windymelt.tfs.infrastructure.ActorSystemComponent
import akka.stream.scaladsl.Source
import scala.concurrent.Future
import akka.stream.scaladsl.Sink
import akka.stream.scaladsl.Keep
import akka.NotUsed
import akka.stream.scaladsl.RunnableGraph

case class DivideByZero(currentCount: Int) extends Exception
class AkkaStreamSpec
    extends com.github.windymelt.tfs.UnitSpec
    with ActorSystemComponent:
  "Source.unfoldAsync" should "behave on failure" in {
    val source: Source[Double | DivideByZero, NotUsed] = Source
      .unfoldAsync(-10) { i =>
        i match
          case 0           => Future.failed(DivideByZero(i))
          case n if n < 10 => Future.successful(Some((n + 1, 10.0 / n)))
          case otherwise   => Future.successful(None)
      }
      .recover { e =>
        e match
          case ee @ DivideByZero(n) => ee
      }

    val result = source.runForeach(sth => println(sth)) // stops when i = 0
    result.foreach { r =>
      r
    }
  }
