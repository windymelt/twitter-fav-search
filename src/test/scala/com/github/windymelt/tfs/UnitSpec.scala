package com.github.windymelt.tfs
import org.scalatest.*
import flatspec.*
import matchers.*

abstract class UnitSpec
    extends AnyFlatSpec
    with should.Matchers
    with OptionValues
    with Inside
    with Inspectors

abstract class AsyncUnitSpec
    extends flatspec.AsyncFlatSpec
    with should.Matchers
    with OptionValues
    with Inside
    with Inspectors
