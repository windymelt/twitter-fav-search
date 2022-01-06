package com.github.windymelt.tfs.infrastructure

import com.github.nscala_time.time.Imports.*
import com.github.windymelt.tfs.domain.Content
import com.github.windymelt.tfs.domain.ContentProvider
import com.github.windymelt.tfs.domain.User
import twitter4j.Status

import java.net.URL

trait TwitterContentConverterComponent:
  val twitterContentConverter = new TwitterContentConverter()
  class TwitterContentConverter:
    def statusToContent(s: Status): Content =
      Content(
        URL(
          s"https://twitter.com/${s.getUser.getScreenName}/${s.getId}"
        ), // 正しいか不明
        ContentProvider.Twitter,
        s.getText,
        User(
          ContentProvider.Twitter,
          s.getUser.getScreenName
        ), // TODO: screen nameだけではなくidも入れたほうがよい？
        new DateTime(s.getCreatedAt)
      )
