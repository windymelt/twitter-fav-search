package com.github.windymelt.tfs.domain

enum RepositoryProvider:
  case Inmemory
  case Twitter

trait ContentRepositoryCtx:
  val repositoryProvider: RepositoryProvider
  val cursor: Any
