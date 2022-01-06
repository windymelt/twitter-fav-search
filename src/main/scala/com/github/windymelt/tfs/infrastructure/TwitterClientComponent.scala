package com.github.windymelt.tfs.infrastructure

import com.github.windymelt.tfs$minustwitter.TwitterClient

trait TwitterClientComponent:
  self: ExecutionContextProviderComponent
    with com.github.windymelt.tfs.infrastructure.ConfigurationComponent =>
  val twitterClient = new TwitterClient(
    config.getString("twitter.oAuthConsumerKey"),
    config.getString("twitter.oAuthConsumerSecret"),
    config.getString("twitter.oAuthAccessToken"),
    config.getString("twitter.oAuthAccessTokenSecret")
  )(ec)
