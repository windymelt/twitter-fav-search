package com.github.windymelt.tfs

import com.github.nscala_time.time.Imports.*
import com.github.windymelt.tfs.application.SaveContentToSearchRepositoryApplicationComponent
import com.github.windymelt.tfs.application.SearchApplicationComponent
import com.github.windymelt.tfs.domain.Content
import com.github.windymelt.tfs.domain.ContentProvider
import com.github.windymelt.tfs.domain.SearchRepositoryComponent
import com.github.windymelt.tfs.domain.SearchRequest
import com.github.windymelt.tfs.domain.SearchResult
import com.github.windymelt.tfs.domain.SimpleQuery
import com.github.windymelt.tfs.domain.User
import com.github.windymelt.tfs.infrastructure.InmemoryComponent
import com.github.windymelt.tfs.infrastructure.InmemorySearchRepositoryComponent

import java.net.URL

object Main
    extends App
    with SearchRepositoryComponent
    with SearchApplicationComponent
    with SaveContentToSearchRepositoryApplicationComponent
    with InmemorySearchRepositoryComponent
    with InmemoryComponent:
  val searchRepository = new InmemorySearchRepository()
  val inmemorySystem = new InmemorySystem()

  val co = Content(
    URL("https://example.com/windymelt"),
    ContentProvider.Twitter,
    "this is a tweet from windymelt!",
    User(ContentProvider.Twitter, "windymelt"),
    DateTime.now()
  )

  // TODO: delegate load/save process into batch system
  val saveResult = saveContentToSearchRepositoryApplication.save(co)
  val result = searchApplication.search(SearchRequest(SimpleQuery("windymelt")))
  println(result)
