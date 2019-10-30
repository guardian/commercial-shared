package com.gu.commercial.display

import com.gu.commercial.display.Cleaner.cleanValue
import org.scalatest.{FlatSpec, Matchers}

// enforces these rules: https://support.google.com/dfp_sb/answer/177381
class CleanerSpec extends FlatSpec with Matchers {

  private def testForIllegalCharacter(c: Char) = cleanValue(s"abc${ c }def") shouldBe "abcdef"

  "A targeting value" should "only have lowercase characters because it's case-insensitive" in {
    cleanValue("Val") shouldBe "val"
  }

  it should """not contain '"'""" in {
    testForIllegalCharacter('"')
  }

  it should "not contain '''" in {
    testForIllegalCharacter('\'')
  }

  it should "not contain '='" in {
    testForIllegalCharacter('=')
  }

  it should "not contain '!'" in {
    testForIllegalCharacter('!')
  }

  it should "not contain '+'" in {
    testForIllegalCharacter('+')
  }

  it should "not contain '#'" in {
    testForIllegalCharacter('#')
  }

  it should "not contain '*'" in {
    testForIllegalCharacter('*')
  }

  it should "not contain '~'" in {
    testForIllegalCharacter('~')
  }

  it should "not contain ';'" in {
    testForIllegalCharacter(';')
  }

  it should "not contain '^'" in {
    testForIllegalCharacter('^')
  }

  it should "not contain '('" in {
    testForIllegalCharacter('(')
  }

  it should "not contain ')'" in {
    testForIllegalCharacter(')')
  }

  it should "not contain '<'" in {
    testForIllegalCharacter('<')
  }

  it should "not contain '>'" in {
    testForIllegalCharacter('>')
  }

  it should "not contain '['" in {
    testForIllegalCharacter('[')
  }

  it should "not contain ']'" in {
    testForIllegalCharacter(']')
  }

  it should "not contain '&'" in {
    testForIllegalCharacter('&')
  }

  it should "be able to contain spaces" in {
    cleanValue("a bc d ef") shouldBe "a bc d ef"
  }

  it should "not start with a space" in {
    cleanValue(" abc") shouldBe "abc"
  }
}
