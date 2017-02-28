package com.gu.commercial.display

object Cleaner {

  val illegalChars = """"'=!+#*~;^()<>[]&"""

  def cleanValue(v: String): String =
    illegalChars.foldLeft(v.trim.toLowerCase) { case (acc, c) => acc.replaceAll(s"\\$c", "") }
}
