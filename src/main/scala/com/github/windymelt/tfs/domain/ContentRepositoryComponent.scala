package com.github.windymelt.tfs.domain

import scala.concurrent.Future

trait ContentRepositoryComponent:
  val contentRepository: ContentRepository
  trait ContentRepository:
    def retrieveLatestContent(
        cursor: ContentRepositoryCtx,
        chunkSize: Int
    ): Future[Option[ContentRepositoryChunkedData]]

    def retrievePastContent(
        cursor: ContentRepositoryCtx,
        chunkSize: Int
    ): Future[Option[ContentRepositoryChunkedData]]

    def preferredChunkSize: Int
