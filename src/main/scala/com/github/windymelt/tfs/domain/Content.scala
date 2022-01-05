package com.github.windymelt.tfs.domain

import java.net.URL
import com.github.nscala_time.time.Imports._
import org.joda.time.DateTime

final case class Content(
    url: URL,
    provider: ContentProvider,
    contentString: String,
    createdBy: User,
    createdAt: DateTime
)
