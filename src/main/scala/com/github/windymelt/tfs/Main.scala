package com.github.windymelt.tfs

import akka.stream.scaladsl.Sink
import com.github.nscala_time.time.Imports.*
import com.github.windymelt.tfs.application.LoadContentFromContentRepositoryApplicationComponent
import com.github.windymelt.tfs.application.SaveContentToSearchRepositoryApplicationComponent
import com.github.windymelt.tfs.application.SearchApplicationComponent
import com.github.windymelt.tfs.domain.Content
import com.github.windymelt.tfs.domain.ContentProvider
import com.github.windymelt.tfs.domain.SearchRepositoryComponent
import com.github.windymelt.tfs.domain.SearchRequest
import com.github.windymelt.tfs.domain.SearchResult
import com.github.windymelt.tfs.domain.SimpleQuery
import com.github.windymelt.tfs.domain.User
import com.github.windymelt.tfs.infrastructure.ActorSystemComponent
import com.github.windymelt.tfs.infrastructure.ConfigurationComponent
import com.github.windymelt.tfs.infrastructure.ExecutionContextProviderComponent
import com.github.windymelt.tfs.infrastructure.InmemoryComponent
import com.github.windymelt.tfs.infrastructure.InmemorySearchRepositoryComponent
import com.github.windymelt.tfs.infrastructure.TwitterClientComponent
import com.github.windymelt.tfs.infrastructure.TwitterContentConverterComponent
import com.github.windymelt.tfs.infrastructure.TwitterContentRepositoryComponent
import com.typesafe.config.ConfigFactory

import java.net.URL
import scala.concurrent.Await

object Main
    extends App
    with ConfigurationComponent
    with ActorSystemComponent
    with SearchRepositoryComponent
    with SearchApplicationComponent
    with SaveContentToSearchRepositoryApplicationComponent
    with LoadContentFromContentRepositoryApplicationComponent
    with InmemorySearchRepositoryComponent
    with InmemoryComponent
    with TwitterClientComponent
    with TwitterContentRepositoryComponent
    with TwitterContentConverterComponent:

  val searchRepository = new InmemorySearchRepository()
  val inmemorySystem = new InmemorySystem()
  val contentRepository = new TwitterContentRepository()

  // TODO: delegate load/save process into batch system
  system.log.info("logging started")
  val loading = loadContentFromContentRepositoryApplication
    .pastContentAsSource(
      ctx = TwitterContentRepositoryCtx(None)
    )
    .log("loader")
    .take(5)
    .map(_._1)
    .runWith(saveContentToSearchRepositoryApplication.searchRepositoryAsSink)

  val printer = loading map { _ =>
    println("printer")
    val result =
      searchApplication.search(SearchRequest(SimpleQuery("NAND")))
    result.contents.map(_.contentString).foreach(system.log.info)
  }
