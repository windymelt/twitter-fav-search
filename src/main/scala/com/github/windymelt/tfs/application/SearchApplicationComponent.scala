package com.github.windymelt.tfs.application

import com.github.windymelt.tfs.domain.SearchRepositoryComponent
import com.github.windymelt.tfs.domain.SearchRequest
import com.github.windymelt.tfs.domain.SearchResult

trait SearchApplicationComponent:
  self: SearchRepositoryComponent =>
  val searchApplication = new SearchApplication

  class SearchApplication:
    def search(searchRequest: SearchRequest): SearchResult =
      searchRepository.search(searchRequest)
