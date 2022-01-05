package com.github.windymelt.tfs.infrastructure

import com.github.windymelt.tfs.domain.Content

trait InmemoryComponent:
  val inmemorySystem: InmemorySystem
  class InmemorySystem:
    var indexedContents: collection.mutable.Seq[Content] =
      collection.mutable.Seq()

    var originalContents: collection.mutable.Seq[Content] =
      collection.mutable.Seq()

    def reset(): Unit =
      indexedContents = collection.mutable.Seq()
      originalContents = collection.mutable.Seq()
