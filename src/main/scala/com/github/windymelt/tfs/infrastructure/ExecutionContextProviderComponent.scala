package com.github.windymelt.tfs.infrastructure

import scala.concurrent.ExecutionContext

trait ExecutionContextProviderComponent:
  implicit val ec: ExecutionContext
