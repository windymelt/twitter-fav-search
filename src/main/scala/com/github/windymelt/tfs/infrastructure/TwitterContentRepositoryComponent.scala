package com.github.windymelt.tfs.infrastructure

import com.github.windymelt.tfs.domain.ContentRepositoryChunkedData
import com.github.windymelt.tfs.domain.ContentRepositoryComponent
import com.github.windymelt.tfs.domain.ContentRepositoryCtx
import com.github.windymelt.tfs.domain.ContentRepositoryThrottling
import com.github.windymelt.tfs.domain.RepositoryProvider
import twitter4j.Status

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

trait TwitterContentRepositoryComponent extends ContentRepositoryComponent:
  self: TwitterClientComponent
    with com.github.windymelt.tfs.infrastructure.ConfigurationComponent
    with TwitterContentConverterComponent
    with ExecutionContextProviderComponent =>

  case class TwitterContentRepositoryCtx(maybePage: Option[Int])
      extends ContentRepositoryCtx:
    val cursor = maybePage
    val repositoryProvider = RepositoryProvider.Twitter

  class TwitterContentRepository(implicit val ec: ExecutionContext)
      extends ContentRepository:
    def retrieveLatestContent(
        cursor: ContentRepositoryCtx,
        chunkSize: Int = preferredChunkSize
    ) = ???

    def retrievePastContent(
        cursor: ContentRepositoryCtx,
        @deprecated chunkSize: Int = preferredChunkSize
    ): Future[Option[ContentRepositoryChunkedData]] = cursor match
      case TwitterContentRepositoryCtx(maybePage) =>
        twitterClient.getFavorites(maybePage) map { res =>
          res match
            case (empty, _) if empty.isEmpty => None
            case (statuses, limit) =>
              val cd =
                ContentRepositoryChunkedData(
                  statuses.map(twitterContentConverter.statusToContent),
                  TwitterContentRepositoryCtx(
                    Some(maybePage.map(_ + 1).getOrElse(2))
                  )
                )
              Some(cd)
        }

    final def preferredChunkSize = 20 // fixed

    import scala.language.postfixOps
    import concurrent.duration.DurationInt

    // XXX: 本来はメソッドごとにスロットリングは異なるが、いまのところfavoriteしか取得していないのでここに置いてある
    override def throttling = Some(ContentRepositoryThrottling(15 minutes, 75))
