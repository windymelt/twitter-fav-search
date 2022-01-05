package com.github.windymelt.tfs.domain

final case class ContentRepositoryChunkedData(
    contents: Seq[Content],
    ctx: ContentRepositoryCtx
)
