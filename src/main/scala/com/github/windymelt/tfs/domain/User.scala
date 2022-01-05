package com.github.windymelt.tfs.domain

enum ContentProvider:
  case Twitter

final case class User(provider: ContentProvider, userId: String)
