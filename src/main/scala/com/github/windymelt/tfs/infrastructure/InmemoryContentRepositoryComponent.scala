package com.github.windymelt.tfs.infrastructure

import com.github.windymelt.tfs.domain.Content
import com.github.windymelt.tfs.domain.ContentRepositoryChunkedData
import com.github.windymelt.tfs.domain.ContentRepositoryComponent
import com.github.windymelt.tfs.domain.ContentRepositoryCtx

import scala.concurrent.Future
import com.github.windymelt.tfs.domain.RepositoryProvider

trait InmemoryContentRepositoryComponent extends ContentRepositoryComponent:
  self: InmemoryComponent =>
  val contentRepository = new InmemoryContentRepository()

  case class InmemoryContentRepositoryCtx(
      cursor: Int
  ) extends ContentRepositoryCtx:
    val repositoryProvider = RepositoryProvider.Inmemory

  class InmemoryContentRepository extends ContentRepository:

    def retrieveLatestContent(
        cursor: ContentRepositoryCtx,
        chunkSize: Int = preferredChunkSize
    ): Future[Option[ContentRepositoryChunkedData]] = cursor match
      case InmemoryContentRepositoryCtx(cur) =>
        val contents =
          inmemorySystem.originalContents.slice(cur - chunkSize, cur).toSeq
        val ctx = InmemoryContentRepositoryCtx(cur - chunkSize)
        contents.isEmpty match // TODO: same as `retrievePastContent`
          case true => Future.successful(None)
          case false =>
            Future.successful(Some(ContentRepositoryChunkedData(contents, ctx)))

    def retrievePastContent(
        cursor: ContentRepositoryCtx =
          InmemoryContentRepository.initialContextForRetrievePastContent,
        chunkSize: Int = preferredChunkSize
    ): Future[Option[ContentRepositoryChunkedData]] = cursor match
      case InmemoryContentRepositoryCtx(cur) =>
        val contents =
          inmemorySystem.originalContents.slice(cur, cur + chunkSize).toSeq
        val ctx = InmemoryContentRepositoryCtx(cur + chunkSize)
        contents.isEmpty match // TODO: emptyではなくchunkSize未満のときに終了させる
          case true => Future.successful(None)
          case false =>
            Future.successful(Some(ContentRepositoryChunkedData(contents, ctx)))

    def preferredChunkSize = 5

  object InmemoryContentRepository:
    def initialContextForRetrievePastContent = InmemoryContentRepositoryCtx(0)
