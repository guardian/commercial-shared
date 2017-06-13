package com.gu.commercial.display

import com.gu.commercial.TestModel
import com.gu.contentapi.client.model.v1.{Content, Section, Tag}
import org.scalatest.{FlatSpec, Matchers, OptionValues}

class AdTargeterSpec extends FlatSpec with Matchers with OptionValues {

  private val surgeLookup = new SurgeLookupService {
    def pageViewsPerMinute(pageId: String): Option[Int] = pageId match {
      case "books/2016/jan/03/murder-for-christmas-mystery-of-author-francis-duncan" => Some(232)
      case "technology/video/2017/feb/07/science-museum-robots-exhibition-backstage" => Some(98)
      case _                                                                         => None
    }
  }

  private val targeter = new AdTargeter(platform = "ng", surgeLookup)

  private def stringify(params: Set[AdTargetParam]): Map[String, Any] =
    AdTargetParam.toMap(params).mapValues {
      case SingleValue(v)     => v
      case MultipleValues(vs) => vs
    }

  private def targetUkEditionPage: Content => Set[AdTargetParam]    = targeter.pageLevelTargetingForContentPage("uk")
  private def targetUkEditionSection: Section => Set[AdTargetParam] = targeter.pageLevelTargetingForSectionFront("uk")
  private def targetUkEditionTag: Tag => Set[AdTargetParam]         = targeter.pageLevelTargetingForTagPage("uk")

  "pageLevelTargetingForContentPage" should "be correct for an article" in {
    val item   = TestModel.getContentItem("TagBrandedContent.json")
    val params = stringify(targetUkEditionPage(item))
    params shouldBe Map(
      "br"      -> "s",
      "co"      -> Set("dom-phillips"),
      "ct"      -> "article",
      "edition" -> "uk",
      "k" -> Set(
        "sustainable-business",
        "brazil",
        "americas",
        "world",
        "coffee",
        "global-development",
        "fair-trade",
        "environment",
        "northernireland"
      ),
      "p"  -> "ng",
      "se" -> Set("spotlight-on-commodities"),
      "su" -> Set("0"),
      "tn" -> Set("news"),
      "url" ->
        "/sustainable-business/2017/jan/04/coffee-rainforest-alliance-utz-brazil-pesticides-exploited-workers-pay"
    )
  }

  it should "be correct for a video page" in {
    val item   = TestModel.getContentItem("VideoContent.json")
    val params = stringify(targetUkEditionPage(item))
    params shouldBe Map(
      "co"      -> Set("jess-gormley", "alex-healey", "oliver-wainwright"),
      "ct"      -> "video",
      "edition" -> "uk",
      "k"       -> Set("robots", "museums", "culture", "technology", "science", "london", "uk/uk"),
      "p"       -> "ng",
      "su"      -> Set("5"),
      "tn"      -> Set("news"),
      "url"     -> "/technology/video/2017/feb/07/science-museum-robots-exhibition-backstage"
    )
  }

  it should "be correct for an Observer page" in {
    val item   = TestModel.getContentItem("ObserverContent.json")
    val params = stringify(targetUkEditionPage(item))
    params shouldBe Map(
      "co"      -> Set("vanessathorpe"),
      "ct"      -> "article",
      "edition" -> "uk",
      "k"       -> Set("crime", "books", "culture", "fiction", "uk/uk"),
      "ob"      -> "t",
      "p"       -> "ng",
      "su"      -> Set("3", "4", "5"),
      "tn"      -> Set("news"),
      "url"     -> "/books/2016/jan/03/murder-for-christmas-mystery-of-author-francis-duncan"
    )
  }

  "pageLevelTargetingForSectionFront" should "be correct for a facia front" in {
    val section = TestModel.getSection("FoundationSection.json")
    val params  = stringify(targetUkEditionSection(section))
    params shouldBe Map(
      "br"      -> "f",
      "ct"      -> "section",
      "edition" -> "uk",
      "k"       -> Set("cities"),
      "p"       -> "ng",
      "url"     -> "/cities"
    )
  }

  it should "be correct for an editionalised facia front" in {
    val section = TestModel.getSection("EditionalisedSection.json")
    val params  = stringify(targetUkEditionSection(section))
    params shouldBe Map(
      "ct"      -> "section",
      "edition" -> "uk",
      "k"       -> Set("culture"),
      "p"       -> "ng",
      "url"     -> "/uk/culture"
    )
  }

  "pageLevelTargetingForTagPage" should "be correct for a facia front" in {
    val tag    = TestModel.getTag("FoundationTag.json")
    val params = stringify(targetUkEditionTag(tag))
    params shouldBe Map(
      "br"      -> "s",
      "ct"      -> "tag",
      "edition" -> "uk",
      "p"       -> "ng",
      "se"      -> Set("spotlight-on-commodities"),
      "url"     -> "/sustainable-business/series/spotlight-on-commodities"
    )
  }

  it should "be correct for a series tag page" in {
    val tag    = TestModel.getTag("SeriesTag.json")
    val params = stringify(targetUkEditionTag(tag))
    params shouldBe Map(
      "ct"      -> "tag",
      "edition" -> "uk",
      "p"       -> "ng",
      "se"      -> Set("new-view-series"),
      "url"     -> "/film/series/new-view-series"
    )
  }

  it should "be correct for a keyword tag page" in {
    val tag    = TestModel.getTag("KeywordTag.json")
    val params = stringify(targeter.pageLevelTargetingForTagPage("us")(tag))
    params shouldBe Map(
      "ct"      -> "tag",
      "edition" -> "us",
      "k"       -> Set("turkey"),
      "p"       -> "ng",
      "url"     -> "/lifeandstyle/turkey"
    )
  }

  "pageLevelTargetingForNetworkFront" should "be correct for a network front" in {
    val params = stringify(targeter.pageLevelTargetingForNetworkFront("au")(networkFrontPath = "/us"))
    params shouldBe Map(
      "ct"      -> "network-front",
      "edition" -> "au",
      "k"       -> Set("us"),
      "p"       -> "ng",
      "url"     -> "/us"
    )
  }

  it should "be correct for International network front" in {
    val params = stringify(targeter.pageLevelTargetingForNetworkFront("international")(networkFrontPath = "/us"))
    params shouldBe Map(
      "ct"      -> "network-front",
      "edition" -> "int",
      "k"       -> Set("us"),
      "p"       -> "ng",
      "url"     -> "/us"
    )
  }

  "pageLevelTargetingForFrontUnknownToCapi" should "be correct for a Facia front not set up in Composer" in {
    val params =
      stringify(targeter.pageLevelTargetingForFrontUnknownToCapi("international")(frontId = "us/tv-and-radio"))
    params shouldBe Map(
      "ct"      -> "section",
      "edition" -> "int",
      "k"       -> Set("tv-and-radio"),
      "p"       -> "ng",
      "url"     -> "/us/tv-and-radio"
    )
  }
}
