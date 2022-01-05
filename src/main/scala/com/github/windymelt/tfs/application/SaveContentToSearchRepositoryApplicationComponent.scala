package com.github.windymelt.tfs.application

import akka.NotUsed
import akka.stream.scaladsl.Sink
import com.github.windymelt.tfs.domain.Content
import com.github.windymelt.tfs.domain.SaveResult
import com.github.windymelt.tfs.domain.SearchRepositoryComponent

trait SaveContentToSearchRepositoryApplicationComponent:
  self: SearchRepositoryComponent =>
  val saveContentToSearchRepositoryApplication = new SaveContentApplication
  class SaveContentApplication:
    def save(content: Content): SaveResult =
      searchRepository.save(content)

    def searchRepositoryAsSink =
      Sink.foreach[Seq[Content]](cs => cs.foreach(save))
