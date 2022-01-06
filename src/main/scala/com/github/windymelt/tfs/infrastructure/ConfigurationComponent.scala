package com.github.windymelt.tfs.infrastructure

import com.typesafe.config.ConfigFactory

trait ConfigurationComponent:
  val config = ConfigFactory.load()
