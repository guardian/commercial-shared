package com.gu.commercial.display

import com.gu.commercial.TestModel
import org.scalatest.{FlatSpec, Matchers, OptionValues}

class AdCallSpec extends FlatSpec with Matchers with OptionValues {

  private val surgeLookup = new SurgeLookupService {
    def pageViewsPerMinute(pageId: String): Option[Int] = pageId match {
      case "books/2016/jan/03/murder-for-christmas-mystery-of-author-francis-duncan" => Some(232)
      case "technology/video/2017/feb/07/science-museum-robots-exhibition-backstage" => Some(98)
      case _ => None
    }
  }

  private val adCall = new AdCall(platform = "ng", surgeLookup)

  private def stringifyKeys(params: Map[AdCallParamKey, String]): Map[String, String] =
    params.map { case (k, v) => k.name -> v }

  "pageLevelContextTargeting: item" should "be correct for an article" in {
    val item = TestModel.getContentItem("TagBrandedContent.json")
    val params = stringifyKeys(adCall.pageLevelContextTargeting(item, edition = "uk"))
    params shouldBe Map(
      "br" -> "s",
      "co" -> "dom-phillips",
      "ct" -> "article",
      "edition" -> "uk",
      "k" -> "sustainable-business,brazil,americas,world,coffee,global-development,fair-trade,environment",
      "p" -> "ng",
      "se" -> "spotlight-on-commodities",
      "su" -> "0",
      "tn" -> "news",
      "url" ->
      "/sustainable-business/2017/jan/04/coffee-rainforest-alliance-utz-brazil-pesticides-exploited-workers-pay"
    )
  }

  it should "be correct for a video page" in {
    val item = TestModel.getContentItem("VideoContent.json")
    val params = stringifyKeys(adCall.pageLevelContextTargeting(item, edition = "uk"))
    params shouldBe Map(
      "co" -> "jess-gormley,alex-healey,oliver-wainwright",
      "ct" -> "video",
      "edition" -> "uk",
      "k" -> "robots,museums,culture,technology,science,london,uk/uk",
      "p" -> "ng",
      "su" -> "5",
      "tn" -> "news",
      "url" -> "/technology/video/2017/feb/07/science-museum-robots-exhibition-backstage"
    )
  }

  it should "be correct for an Observer page" in {
    val item = TestModel.getContentItem("ObserverContent.json")
    val params = stringifyKeys(adCall.pageLevelContextTargeting(item, edition = "uk"))
    params shouldBe Map(
      "co" -> "vanessathorpe",
      "ct" -> "article",
      "edition" -> "uk",
      "k" -> "crime,books,culture,fiction,uk/uk",
      "ob" -> "t",
      "p" -> "ng",
      "su" -> "3,4,5",
      "tn" -> "news",
      "url" -> "/books/2016/jan/03/murder-for-christmas-mystery-of-author-francis-duncan"
    )
  }

  "pageLevelContextTargeting: section" should "be correct for a facia front" in {
    val section = TestModel.getSection("BrandedSection.json")
    val params = stringifyKeys(adCall.pageLevelContextTargeting(section, edition = "uk"))
    params shouldBe Map(
      "br" -> "f",
      "ct" -> "section",
      "edition" -> "uk",
      "k" -> "cities",
      "p" -> "ng",
      "url" -> "/cities"
    )
  }

  it should "be correct for an editionalised facia front" in {
    val section = TestModel.getSection("EditionalisedSection.json")
    val params = stringifyKeys(adCall.pageLevelContextTargeting(section, edition = "uk"))
    params shouldBe Map(
      "ct" -> "section",
      "edition" -> "uk",
      "k" -> "culture",
      "p" -> "ng",
      "url" -> "/uk/culture"
    )
  }

  "pageLevelContextTargeting: tag" should "be correct for a facia front" in {
    val tag = TestModel.getTag("BrandedTag.json")
    val params = stringifyKeys(adCall.pageLevelContextTargeting(tag, edition = "uk"))
    params shouldBe Map(
      "br" -> "s",
      "ct" -> "tag",
      "edition" -> "uk",
      "p" -> "ng",
      "se" -> "spotlight-on-commodities",
      "url" -> "/sustainable-business/series/spotlight-on-commodities"
    )
  }

  it should "be correct for a series tag page" in {
    val tag = TestModel.getTag("SeriesTag.json")
    val params = stringifyKeys(adCall.pageLevelContextTargeting(tag, edition = "uk"))
    params shouldBe Map(
      "ct" -> "tag",
      "edition" -> "uk",
      "p" -> "ng",
      "se" -> "new-view-series",
      "url" -> "/film/series/new-view-series"
    )
  }

  it should "be correct for a keyword tag page" in {
    val tag = TestModel.getTag("KeywordTag.json")
    val params = stringifyKeys(adCall.pageLevelContextTargeting(tag, edition = "us"))
    params shouldBe Map(
      "ct" -> "tag",
      "edition" -> "us",
      "k" -> "turkey",
      "p" -> "ng",
      "url" -> "/lifeandstyle/turkey"
    )
  }

  "pageLevelContextTargeting: networkFront" should "be correct for a network front" in {
    val params = stringifyKeys(adCall.pageLevelContextTargeting(networkFrontPath = "/us", edition = "au"))
    params shouldBe Map(
      "ct" -> "network-front",
      "edition" -> "au",
      "k" -> "us",
      "p" -> "ng",
      "url" -> "/us"
    )
  }
}
