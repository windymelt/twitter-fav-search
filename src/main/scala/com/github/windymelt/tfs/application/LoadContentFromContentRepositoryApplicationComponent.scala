package com.github.windymelt.tfs.application

import akka.NotUsed
import akka.stream.scaladsl.Source
import com.github.windymelt.tfs.domain.Content
import com.github.windymelt.tfs.domain.ContentRepositoryChunkedData
import com.github.windymelt.tfs.domain.ContentRepositoryComponent
import com.github.windymelt.tfs.domain.ContentRepositoryCtx

trait LoadContentFromContentRepositoryApplicationComponent:
  self: ContentRepositoryComponent with ActorSystemComponent =>
  val loadContentFromContentRepositoryApplication = new LoadContentApplication()
  class LoadContentApplication:
    def pastContentAsSource(
        ctx: ContentRepositoryCtx
    ): Source[(Seq[Content], ContentRepositoryCtx), NotUsed] =
      Source.unfoldAsync(ctx) { currentCtx =>
        contentRepository.retrievePastContent(
          currentCtx,
          contentRepository.preferredChunkSize
        ) map (_.map(chunk => (chunk.ctx, (chunk.contents, chunk.ctx))))
      }
