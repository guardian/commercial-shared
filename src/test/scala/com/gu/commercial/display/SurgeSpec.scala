package com.gu.commercial.display

import org.scalatest.OptionValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class SurgeSpec extends AnyFlatSpec with Matchers with OptionValues {

  "bucket" should "put page views per minute into a range of bucket values" in {
    Surge.bucket(Some(500)) shouldBe Seq(1, 2, 3, 4, 5)
    Surge.bucket(Some(401)) shouldBe Seq(1, 2, 3, 4, 5)
    Surge.bucket(Some(301)) shouldBe Seq(2, 3, 4, 5)
    Surge.bucket(Some(201)) shouldBe Seq(3, 4, 5)
    Surge.bucket(Some(101)) shouldBe Seq(4, 5)
    Surge.bucket(Some(99)) shouldBe Seq(5)
    Surge.bucket(Some(49)) shouldBe Seq(0)
    Surge.bucket(None) shouldBe Seq(0)
  }
}
