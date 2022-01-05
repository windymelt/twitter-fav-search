package com.github.windymelt.tfs.application

import com.github.windymelt.tfs.UnitSpec
import com.github.windymelt.tfs.infrastructure.InmemoryComponent
import com.github.windymelt.tfs.infrastructure.InmemorySearchRepositoryComponent
import com.typesafe.config.ConfigFactory
import org.scalatest.BeforeAndAfter
import com.github.windymelt.tfs.TestHelper
import akka.stream.scaladsl.Source
import com.github.windymelt.tfs.domain.SearchRequest
import com.github.windymelt.tfs.domain.SimpleQuery

class SaveContentToSearchRepositoryApplicationComponentSpec
    extends UnitSpec
    with BeforeAndAfter
    with InmemoryComponent
    with InmemorySearchRepositoryComponent
    with com.github.windymelt.tfs.infrastructure.ActorSystemComponent
    with com.github.windymelt.tfs.application.SaveContentToSearchRepositoryApplicationComponent:
  ConfigFactory.load()

  val inmemorySystem = new InmemorySystem()
  val searchRepository = new InmemorySearchRepository()

  before {
    inmemorySystem.reset()
  }

  "searchRepositoryAsSink" should "accept 100 content" in {
    val contents = 1 to 100 map TestHelper.genDummyContent
    val contentsPer10 = contents.grouped(10)
    val source = Source.fromIterator(() => contentsPer10)
    val g =
      source.to(saveContentToSearchRepositoryApplication.searchRepositoryAsSink)
    g.run()
    Thread.sleep(100)
    inmemorySystem.indexedContents.size shouldBe 100
    searchRepository
      .search(SearchRequest(SimpleQuery("content 42")))
      .contents should have size 1
  }
