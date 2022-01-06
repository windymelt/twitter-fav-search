package com.github.windymelt.tfs.infrastructure

import org.scalatest.*
import com.github.windymelt.tfs.UnitSpec
import com.github.windymelt.tfs.domain.Content
import java.net.URL
import com.github.windymelt.tfs.domain.ContentProvider
import com.github.windymelt.tfs.domain.User
import com.github.windymelt.tfs.AsyncUnitSpec
import scala.concurrent.Future
import com.github.windymelt.tfs.TestHelper

class InmemoryContentRepositoryComponentSpec
    extends AsyncUnitSpec
    with org.scalatest.BeforeAndAfter
    with com.github.windymelt.tfs.infrastructure.InmemoryComponent
    with InmemoryContentRepositoryComponent:
  val inmemorySystem = new InmemorySystem()

  before {
    inmemorySystem.reset()
  }

  "InmemoryContentRepository.retrievePastContent" should "return first chunk" in {
    inmemorySystem.originalContents =
      collection.mutable.Seq(1 to 100*) map TestHelper.genDummyContent
    val firstResult = contentRepository.retrievePastContent(
      cursor = InmemoryContentRepository.initialContextForRetrievePastContent,
      chunkSize = 2
    )
    firstResult map {
      _.get.contents shouldBe inmemorySystem.originalContents.slice(0, 2).toSeq
    }
  }

  it should "return second..5th chunks" in {
    inmemorySystem.originalContents =
      collection.mutable.Seq(1 to 100*) map TestHelper.genDummyContent

    val initialCtx =
      InmemoryContentRepository.initialContextForRetrievePastContent
    for
      fr <- contentRepository.retrievePastContent(initialCtx, 2)
      _ <- fr.get.contents shouldBe inmemorySystem.originalContents
        .slice(0, 2)
        .toSeq
      sr <- contentRepository.retrievePastContent(fr.get.ctx, 2)
      _ <- sr.get.contents shouldBe inmemorySystem.originalContents
        .slice(2, 4)
        .toSeq
      tr <- contentRepository.retrievePastContent(sr.get.ctx, 2)
      _ <- tr.get.contents shouldBe inmemorySystem.originalContents
        .slice(4, 6)
        .toSeq
      fr <- contentRepository.retrievePastContent(tr.get.ctx, 2)
      _ <- fr.get.contents shouldBe inmemorySystem.originalContents
        .slice(6, 8)
        .toSeq
      fifr <- contentRepository.retrievePastContent(fr.get.ctx, 2)
    yield fifr.get.contents shouldBe inmemorySystem.originalContents
      .slice(8, 10)
      .toSeq
  }

  it should "ends up with None when end of sequence" in {
    inmemorySystem.originalContents =
      collection.mutable.Seq(1 to 10*) map TestHelper.genDummyContent

    val initialCtx =
      InmemoryContentRepository.initialContextForRetrievePastContent
    for
      first <- contentRepository.retrievePastContent(initialCtx, 5)
      _ <- first.get.contents shouldBe inmemorySystem.originalContents
        .slice(0, 5)
        .toSeq
      second <- contentRepository.retrievePastContent(first.get.ctx, 5)
      _ <- second.get.contents shouldBe inmemorySystem.originalContents
        .slice(5, 10)
        .toSeq
      third <- contentRepository.retrievePastContent(second.get.ctx, 5)
    yield third shouldBe None
  }

  "InmemoryContentRepository.retrieveLatestContent" should "return first chunk" in {
    inmemorySystem.originalContents =
      collection.mutable.Seq(1 until 100*) map TestHelper.genDummyContent
    val firstResult =
      contentRepository.retrieveLatestContent(
        cursor = InmemoryContentRepositoryCtx(100),
        chunkSize = 2
      )
    firstResult map {
      _.get.contents shouldBe inmemorySystem.originalContents
        .slice(98, 100)
        .toSeq
    }
  }

  it should "return second..5th chunks" in {
    inmemorySystem.originalContents =
      collection.mutable.Seq(1 until 100*) map TestHelper.genDummyContent

    val initialCtx = InmemoryContentRepositoryCtx(100)
    for
      fr <- contentRepository.retrieveLatestContent(initialCtx, 2)
      _ <- fr.get.contents shouldBe inmemorySystem.originalContents
        .slice(98, 100)
        .toSeq
      sr <- contentRepository.retrieveLatestContent(fr.get.ctx, 2)
      _ <- sr.get.contents shouldBe inmemorySystem.originalContents
        .slice(96, 98)
        .toSeq
      tr <- contentRepository.retrieveLatestContent(sr.get.ctx, 2)
      _ <- tr.get.contents shouldBe inmemorySystem.originalContents
        .slice(94, 96)
        .toSeq
      fr <- contentRepository.retrieveLatestContent(tr.get.ctx, 2)
      _ <- fr.get.contents shouldBe inmemorySystem.originalContents
        .slice(92, 94)
        .toSeq
      fifr <- contentRepository.retrieveLatestContent(fr.get.ctx, 2)
    yield fifr.get.contents shouldBe inmemorySystem.originalContents
      .slice(90, 92)
      .toSeq
  }

  it should "ends up with None when end of sequence" in {
    inmemorySystem.originalContents =
      collection.mutable.Seq(1 to 10*) map TestHelper.genDummyContent

    val initialCtx = InmemoryContentRepositoryCtx(10)
    for
      first <- contentRepository.retrieveLatestContent(initialCtx, 5)
      _ <- first.get.contents shouldBe inmemorySystem.originalContents
        .slice(5, 10)
        .toSeq
      second <- contentRepository.retrieveLatestContent(first.get.ctx, 5)
      _ <- second.get.contents shouldBe inmemorySystem.originalContents
        .slice(0, 5)
        .toSeq
      third <- contentRepository.retrieveLatestContent(second.get.ctx, 5)
    yield third shouldBe None
  }
