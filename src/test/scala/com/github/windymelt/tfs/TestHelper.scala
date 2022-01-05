package com.github.windymelt.tfs

import com.github.windymelt.tfs.domain.Content
import java.net.URL
import com.github.windymelt.tfs.domain.ContentProvider
import com.github.windymelt.tfs.domain.User
import com.github.nscala_time.time.Imports.*

object TestHelper:
  def genDummyContent(id: Int): Content = Content(
    URL(s"http://example.com/$id"),
    ContentProvider.Twitter,
    s"content $id",
    User(ContentProvider.Twitter, s"user$id"),
    DateTime.now()
  )
