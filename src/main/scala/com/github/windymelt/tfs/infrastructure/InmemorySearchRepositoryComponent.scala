package com.github.windymelt.tfs.infrastructure

import com.github.windymelt.tfs.domain.SearchRepositoryComponent
import com.github.windymelt.tfs.domain.SearchRequest
import com.github.windymelt.tfs.domain.SearchResult
import com.github.windymelt.tfs.domain.SimpleQuery
import com.github.windymelt.tfs.domain.Content
import com.github.windymelt.tfs.domain.SaveResult

trait InmemorySearchRepositoryComponent extends SearchRepositoryComponent:
  self: InmemoryComponent =>
  object InmemorySaveOk extends SaveResult
  class InmemorySearchRepository extends SearchRepository:
    def search(request: SearchRequest): SearchResult =
      request.query match
        case SimpleQuery(qs) =>
          val cs =
            inmemorySystem.indexedContents.filter(_.contentString.contains(qs))
          SearchResult(cs.toSeq)
        case otherwise => ???

    def save(content: Content) =
      inmemorySystem.indexedContents :+= content
      InmemorySaveOk
