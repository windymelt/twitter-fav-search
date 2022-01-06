package com.github.windymelt.`tfs-twitter`

import twitter4j.AsyncTwitterFactory
import twitter4j.Paging
import twitter4j.ResponseList
import twitter4j.Status
import twitter4j.Twitter
import twitter4j.TwitterAdapter
import twitter4j.TwitterException
import twitter4j.TwitterFactory
import twitter4j.TwitterMethod
import twitter4j.conf.ConfigurationBuilder

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.Promise

import collection.JavaConverters.*

case class RateLimit(
    limit: Int,
    remaining: Int,
    resetTimeInSec: Int,
    secondsUntilReset: Int
)

class TwitterClient(
    oAuthConsumerKey: String,
    oAuthConsumerSecret: String,
    oAuthAccessToken: String,
    oAuthAccessTokenSecret: String
)(implicit ec: ExecutionContext):
  // Setup credentials
  private val cb = new ConfigurationBuilder
  cb.setDebugEnabled(true)
    .setOAuthConsumerKey(oAuthConsumerKey)
    .setOAuthConsumerSecret(oAuthConsumerSecret)
    .setOAuthAccessToken(oAuthAccessToken)
    .setOAuthAccessTokenSecret(oAuthAccessTokenSecret)

  private val tf = new AsyncTwitterFactory(cb.build)

  def getFavorites(): Future[(Seq[Status], RateLimit)] =
    getFavorites(None)

  def getFavorites(
      page: Int
  ): Future[(Seq[Status], RateLimit)] =
    getFavorites(
      Some(page)
    )

  def getFavorites(
      page: Option[Int]
  ): Future[(Seq[Status], RateLimit)] =
    val asyncTwitter = tf.getInstance
    val resultPromise = Promise[ResponseList[Status]]()
    val listener = new TwitterAdapter():
      override def gotFavorites(rs: ResponseList[Status]): Unit =
        resultPromise.success(rs)
      override def onException(
          e: TwitterException,
          method: TwitterMethod
      ): Unit =
        resultPromise.failure(e)

    asyncTwitter.addListener(listener)
    page match
      case Some(p) =>
        // Returns 20 favorites. Chunk size is fixed.
        asyncTwitter.getFavorites(
          "windymelt",
          Paging(p)
        )
      case None => asyncTwitter.getFavorites("windymelt")

    resultPromise.future.map { result =>
      val rateLimit = result.getRateLimitStatus
      val rateLimitObject = RateLimit(
        rateLimit.getLimit,
        rateLimit.getRemaining,
        rateLimit.getResetTimeInSeconds,
        rateLimit.getSecondsUntilReset
      )
      val cloned = result.asScala.toList
      (cloned, rateLimitObject)
    }
