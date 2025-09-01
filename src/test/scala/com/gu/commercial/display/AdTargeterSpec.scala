package com.gu.commercial.display

import com.gu.commercial.TestModel
import com.gu.commercial.display.AdTargetParam.toMap
import com.gu.contentapi.client.model.v1.{Content, Section, Tag}
import org.scalatest.OptionValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class AdTargeterSpec extends AnyFlatSpec with Matchers with OptionValues {

  private val surgeLookup = new SurgeLookupService {
    def pageViewsPerMinute(pageId: String): Option[Int] = pageId match {
      case "books/2016/jan/03/murder-for-christmas-mystery-of-author-francis-duncan" => Some(232)
      case "technology/video/2017/feb/07/science-museum-robots-exhibition-backstage" => Some(98)
      case _                                                                         => None
    }
  }

  private val targeter = new AdTargeter(platform = "ng", surgeLookup)

  private def targetUkEditionPage: Content => Set[AdTargetParam]    = targeter.pageLevelTargetingForContentPage("uk")
  private def targetUkEditionSection: Section => Set[AdTargetParam] = targeter.pageLevelTargetingForSectionFront("uk")
  private def targetUkEditionTag: Tag => Set[AdTargetParam]         = targeter.pageLevelTargetingForTagPage("uk")

  "pageLevelTargetingForContentPage" should "be correct for an article" in {
    val item   = TestModel.getContentItem("TagBrandedContent.json")
    val params = toMap(targetUkEditionPage(item))
    params shouldBe Map(
      "br"      -> SingleValue("s"),
      "co"      -> MultipleValues(Set("dom-phillips")),
      "ct"      -> SingleValue("article"),
      "edition" -> SingleValue("uk"),
      "k" -> MultipleValues(
        Set(
          "sustainable-business",
          "brazil",
          "americas",
          "world",
          "coffee",
          "global-development",
          "fair-trade",
          "environment",
          "northernireland"
        )
      ),
      "p"  -> SingleValue("ng"),
      "se" -> MultipleValues(Set("spotlight-on-commodities")),
      "su" -> MultipleValues(Set("0")),
      "tn" -> MultipleValues(Set("news")),
      "url" -> SingleValue(
        "/sustainable-business/2017/jan/04/coffee-rainforest-alliance-utz-brazil-pesticides-exploited-workers-pay"
      ),
      "s" -> SingleValue("sustainable-business"),
      "sens" -> SingleValue("false")
    )
    params.get("rp") shouldBe None
  }

  "pageLevelTargetingForContentPage with rp" should "be correct for an article" in {
    val item   = TestModel.getContentItem("TagBrandedContent.json")
    def targetWithRpUkEditionPage: Content => Set[AdTargetParam]    = targeter.pageLevelTargetingForContentPage("uk",  Some(RenderingPlatformParam("apps-rendering")))

    val params = toMap(targetWithRpUkEditionPage(item))
    params shouldBe Map(
      "br"      -> SingleValue("s"),
      "co"      -> MultipleValues(Set("dom-phillips")),
      "ct"      -> SingleValue("article"),
      "edition" -> SingleValue("uk"),
      "k" -> MultipleValues(
        Set(
          "sustainable-business",
          "brazil",
          "americas",
          "world",
          "coffee",
          "global-development",
          "fair-trade",
          "environment",
          "northernireland"
        )
      ),
      "p"  -> SingleValue("ng"),
      "rp"  -> SingleValue("apps-rendering"),
      "se" -> MultipleValues(Set("spotlight-on-commodities")),
      "su" -> MultipleValues(Set("0")),
      "tn" -> MultipleValues(Set("news")),
      "url" -> SingleValue(
        "/sustainable-business/2017/jan/04/coffee-rainforest-alliance-utz-brazil-pesticides-exploited-workers-pay"
      ),
      "s" -> SingleValue("sustainable-business"),
      "sens" -> SingleValue("false")
    )
  }

  "pageLevelTargetingForContentPage" should "have a correct 'sens' param" in {
    val item   = TestModel.getContentItem("SensitiveContent.json")
    val params = toMap(targetUkEditionPage(item))
    params shouldBe Map(
      "br"      -> SingleValue("s"),
      "co"      -> MultipleValues(Set("dom-phillips")),
      "ct"      -> SingleValue("article"),
      "edition" -> SingleValue("uk"),
      "k" -> MultipleValues(
        Set(
          "sustainable-business",
          "brazil",
          "americas",
          "world",
          "coffee",
          "global-development",
          "fair-trade",
          "environment",
          "northernireland"
        )
      ),
      "p"  -> SingleValue("ng"),
      "rp"  -> SingleValue("apps-rendering"),
      "se" -> MultipleValues(Set("spotlight-on-commodities")),
      "su" -> MultipleValues(Set("0")),
      "tn" -> MultipleValues(Set("news")),
      "url" -> SingleValue(
        "/sustainable-business/2017/jan/04/coffee-rainforest-alliance-utz-brazil-pesticides-exploited-workers-pay"
      ),
      "s" -> SingleValue("sustainable-business"),
      "sens" -> SingleValue("true")
    )
  }

  it should "be correct for a video page" in {
    val item   = TestModel.getContentItem("VideoContent.json")
    val params = toMap(targetUkEditionPage(item))
    params shouldBe Map(
      "co"      -> MultipleValues(Set("jess-gormley", "alex-healey", "oliver-wainwright")),
      "ct"      -> SingleValue("video"),
      "edition" -> SingleValue("uk"),
      "k"       -> MultipleValues(Set("robots", "museums", "culture", "technology", "science", "london", "uk/uk")),
      "p"       -> SingleValue("ng"),
      "su"      -> MultipleValues(Set("5")),
      "tn"      -> MultipleValues(Set("news")),
      "url"     -> SingleValue("/technology/video/2017/feb/07/science-museum-robots-exhibition-backstage"),
      "s"       -> SingleValue("technology"),
      "sens"    -> SingleValue("false")
    )
  }

  it should "be correct for an Observer page" in {
    val item   = TestModel.getContentItem("ObserverContent.json")
    val params = toMap(targetUkEditionPage(item))
    params shouldBe Map(
      "co"      -> MultipleValues(Set("vanessathorpe")),
      "ct"      -> SingleValue("article"),
      "edition" -> SingleValue("uk"),
      "k"       -> MultipleValues(Set("crime", "books", "culture", "fiction", "uk/uk")),
      "ob"      -> SingleValue("t"),
      "p"       -> SingleValue("ng"),
      "su"      -> MultipleValues(Set("3", "4", "5")),
      "tn"      -> MultipleValues(Set("news")),
      "url"     -> SingleValue("/books/2016/jan/03/murder-for-christmas-mystery-of-author-francis-duncan"),
      "s"       -> SingleValue("books"),
      "sens"    -> SingleValue("false")
    )
  }

  "pageLevelTargetingForSectionFront" should "be correct for a facia front" in {
    val section = TestModel.getSection("FoundationSection.json")
    val params  = toMap(targetUkEditionSection(section))
    params shouldBe Map(
      "br"      -> SingleValue("f"),
      "ct"      -> SingleValue("section"),
      "edition" -> SingleValue("uk"),
      "k"       -> MultipleValues(Set("cities")),
      "p"       -> SingleValue("ng"),
      "url"     -> SingleValue("/cities"),
      "s"       -> SingleValue("cities"),
    )
  }

  it should "be correct for an editionalised facia front" in {
    val section = TestModel.getSection("EditionalisedSection.json")
    val params  = toMap(targetUkEditionSection(section))
    params shouldBe Map(
      "ct"      -> SingleValue("section"),
      "edition" -> SingleValue("uk"),
      "k"       -> MultipleValues(Set("culture")),
      "p"       -> SingleValue("ng"),
      "url"     -> SingleValue("/uk/culture"),
      "s"       -> SingleValue("culture")
    )
  }

  "pageLevelTargetingForTagPage" should "be correct for a facia front" in {
    val tag    = TestModel.getTag("FoundationTag.json")
    val params = toMap(targetUkEditionTag(tag))
    params shouldBe Map(
      "br"      -> SingleValue("s"),
      "ct"      -> SingleValue("tag"),
      "edition" -> SingleValue("uk"),
      "p"       -> SingleValue("ng"),
      "se"      -> MultipleValues(Set("spotlight-on-commodities")),
      "url"     -> SingleValue("/sustainable-business/series/spotlight-on-commodities"),
      "s"       -> SingleValue("sustainable-business")
    )
  }

  it should "be correct for a series tag page" in {
    val tag    = TestModel.getTag("SeriesTag.json")
    val params = toMap(targetUkEditionTag(tag))
    params shouldBe Map(
      "ct"      -> SingleValue("tag"),
      "edition" -> SingleValue("uk"),
      "p"       -> SingleValue("ng"),
      "se"      -> MultipleValues(Set("new-view-series")),
      "url"     -> SingleValue("/film/series/new-view-series"),
      "s"       -> SingleValue("film")
    )
  }

  it should "be correct for a keyword tag page" in {
    val tag    = TestModel.getTag("KeywordTag.json")
    val params = toMap(targeter.pageLevelTargetingForTagPage("us")(tag))
    params shouldBe Map(
      "ct"      -> SingleValue("tag"),
      "edition" -> SingleValue("us"),
      "k"       -> MultipleValues(Set("turkey")),
      "p"       -> SingleValue("ng"),
      "url"     -> SingleValue("/lifeandstyle/turkey"),
      "s"       -> SingleValue("lifeandstyle")
    )
  }

  it should "be correct for a 'type' tag page" in {
      val tag = TestModel.getTag("TypeTag.json")
      val params = toMap(targeter.pageLevelTargetingForTagPage("uk")(tag))
      params shouldBe Map(
        "ct" -> SingleValue("tag"),
        "edition" -> SingleValue("uk"),
        "url" -> SingleValue("/video"),
        "p" -> SingleValue("ng"),
        "s" -> SingleValue("video")
      )
    }

  "pageLevelTargetingForNetworkFront" should "be correct for a network front" in {
    val params = toMap(targeter.pageLevelTargetingForNetworkFront("au")(networkFrontPath = "/us"))
    params shouldBe Map(
      "ct"      -> SingleValue("network-front"),
      "edition" -> SingleValue("au"),
      "k"       -> MultipleValues(Set("us")),
      "p"       -> SingleValue("ng"),
      "url"     -> SingleValue("/us"),
      "s"       -> SingleValue("us")
    )
  }

  it should "be correct for International network front" in {
    val params = toMap(targeter.pageLevelTargetingForNetworkFront("international")(networkFrontPath = "/us"))
    params shouldBe Map(
      "ct"      -> SingleValue("network-front"),
      "edition" -> SingleValue("int"),
      "k"       -> MultipleValues(Set("us")),
      "p"       -> SingleValue("ng"),
      "url"     -> SingleValue("/us"),
      "s"       -> SingleValue("us")
    )
  }

  "pageLevelTargetingForFrontUnknownToCapi" should "be correct for a Facia front not set up in Composer" in {
    val params =
      toMap(targeter.pageLevelTargetingForFrontUnknownToCapi("international")(frontId = "us/tv-and-radio"))
    params shouldBe Map(
      "ct"      -> SingleValue("section"),
      "edition" -> SingleValue("int"),
      "k"       -> MultipleValues(Set("tv-and-radio")),
      "p"       -> SingleValue("ng"),
      "url"     -> SingleValue("/us/tv-and-radio"),
      "s"       -> SingleValue("tv-and-radio")
    )
  }
}
