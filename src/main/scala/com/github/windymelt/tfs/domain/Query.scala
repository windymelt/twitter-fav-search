package com.github.windymelt.tfs.domain

abstract sealed class Query

final case class SimpleQuery(queryString: String) extends Query