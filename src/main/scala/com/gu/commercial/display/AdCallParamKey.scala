package com.gu.commercial.display

sealed trait AdCallParamKey {
  def name: String
}

case object AuthorKey extends AdCallParamKey {val name = "co" }
case object BlogKey extends AdCallParamKey {val name = "bl" }
case object BrandingKey extends AdCallParamKey {val name = "br" }
case object ContentTypeKey extends AdCallParamKey {val name = "ct" }
case object EditionKey extends AdCallParamKey {val name = "edition" }
case object KeywordKey extends AdCallParamKey {val name = "k" }
case object ObserverKey extends AdCallParamKey {val name = "ob" }
case object PathKey extends AdCallParamKey {val name = "url" }
case object PlatformKey extends AdCallParamKey {val name = "p" }
case object SeriesKey extends AdCallParamKey {val name = "se" }
case object ShortUrlKey extends AdCallParamKey {val name = "sh" }
case object SurgeLevelKey extends AdCallParamKey {val name = "su" }
case object ToneKey extends AdCallParamKey {val name = "tn" }
