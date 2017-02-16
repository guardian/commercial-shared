package com.gu.commercial.display

object Surge {

  def bucket(pvsPerMin:Option[Int]):Seq[Int] = {

    val maxLevel = pvsPerMin match {
      case Some(pv) if pv >= 400 => 1
      case Some(pv) if pv >= 300 => 2
      case Some(pv) if pv >= 200 => 3
      case Some(pv) if pv >= 100 => 4
      case Some(pv) if pv >= 50 => 5
      case _ => 0
      }

    if (maxLevel == 0) Seq(0)
    else Seq.range(maxLevel, 6)
  }
}
