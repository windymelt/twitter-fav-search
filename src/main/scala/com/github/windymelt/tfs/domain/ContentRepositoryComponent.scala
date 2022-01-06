package com.github.windymelt.tfs.domain

import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration

case class ContentRepositoryThrottling(
    duration: FiniteDuration,
    requestCount: Int
)

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
    def throttling: Option[ContentRepositoryThrottling] = None
