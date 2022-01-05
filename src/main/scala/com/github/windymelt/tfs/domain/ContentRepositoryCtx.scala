package com.github.windymelt.tfs.domain

enum RepositoryProvider:
  case Inmemory

trait ContentRepositoryCtx:
  val repositoryProvider: RepositoryProvider
  val cursor: Any
