package com.gu.commercial.display

object Cleaner {

  val illegalChars = """"'=!+#*~;^()<>[]&"""

  val illegalCharRegex = {
    val escapedChars = illegalChars.replaceAll(".", "\\\\" + "$0")
    s"[$escapedChars]".r
  }

  def cleanValue(v: String): String = illegalCharRegex.replaceAllIn(v.trim.toLowerCase, "")
}
