package com.github.windymelt.tfs.domain

trait SearchRepositoryComponent:
  val searchRepository: SearchRepository
  trait SearchRepository:
    def search(request: SearchRequest): SearchResult
    def save(content: Content): SaveResult
