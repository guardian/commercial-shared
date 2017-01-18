package com.gu.commercial.branding

import com.gu.commercial.branding.TestModel.StubItem
import net.liftweb.json
import net.liftweb.json.JsonAST.JField
import org.scalatest.{FlatSpec, Matchers, OptionValues}

import scala.io.Source

class BrandingFinderSpec extends FlatSpec with Matchers with OptionValues {

  private implicit val jsonFormats = json.DefaultFormats

  private def brandedItem(fileName: String) = {
    val s = Source.fromURL(getClass.getResource(s"/$fileName")).mkString
    val j = json.parse(s)
    j.transform {
      case JField("sponsorshipType", v) => JField("sponsorshipTypeName", v)
      case JField("webPublicationDate", v) => JField("publicationDateText", v)
      case JField("publishedSince", v) => JField("publishedSinceText", v)
    }.extract[StubItem]
  }

  private def getTagBrandedItem = brandedItem("TagBrandedContent.json")
  private def getMultipleTagBrandedItem = brandedItem("TagBrandedContent-MultipleBrands.json")
  private def getSectionBrandedItem = brandedItem("SectionBrandedContent.json")
  private def getSectionAndTagBrandedItem = brandedItem("SectionAndTagBrandedContent.json")
  private def getEditionTargetedTagBrandedItem = brandedItem("EditionTargetedTagBrandedContent.json")
  private def getBeforeDateTargetedTagBrandedItem = brandedItem("BeforeDateTargetedTagBrandedContent.json")
  private def getAfterDateTargetedTagBrandedItem = brandedItem("AfterDateTargetedTagBrandedContent.json")

  "findItemBranding" should "give branding of tag for content with a single branded tag" in {
    val item = getTagBrandedItem
    val branding = BrandingFinder.findItemBranding(item, edition = "uk")
    branding.value should be(Branding(
      brandingType = Sponsored,
      sponsor = "Fairtrade Foundation",
      logo = Logo(
        src = "https://static.theguardian.com/commercial/sponsor/sustainable/series/spotlight-commodities/logo.png",
        width = None,
        height = None,
      link = "http://www.fairtrade.org.uk/"
      ),
      logoForDarkBackground = None
    ))
  }

  it should "give branding of first matching branded tag for content with multiple branded tags" in {
    val item = getMultipleTagBrandedItem
    val branding = BrandingFinder.findItemBranding(item, edition = "uk")
    branding.value should be(Branding(
      brandingType = Sponsored,
      sponsor = "Fairtrade Foundation",
      logo = Logo(
        src = "https://static.theguardian.com/commercial/sponsor/sustainable/series/spotlight-commodities/logo.png",
        width = None,
        height = None,
        link = "http://www.fairtrade.org.uk/"
      ),
      logoForDarkBackground = None
    ))
  }

  it should "give section branding for content in a branded section" in {
    val item = getSectionBrandedItem
    val branding = BrandingFinder.findItemBranding(item, edition = "uk")
    branding.value should be(Branding(
      brandingType = Sponsored,
      sponsor = "Rockefeller Foundation",
      logo = Logo(
        src = "https://static.theguardian.com/commercial/sponsor/cities/cities/logo.png",
        width = None,
        height = None,
        link = "http://www.100resilientcities.org/"
      ),
      logoForDarkBackground = Some(Logo(
        src = "https://static.theguardian.com/commercial/sponsor/19/Oct/2016/4369caea-6271-4ddf-ad67-Rock_white.png",
        width = None,
        height = None,
        link = "http://www.100resilientcities.org/"
      ))
    ))
  }

  it should "give branding of tag for content in a branded section and with a branded tag" in {
    val item = getSectionAndTagBrandedItem
    val branding = BrandingFinder.findItemBranding(item, edition = "uk")
    branding.value should be(Branding(
      brandingType = Sponsored,
      sponsor = "Fairtrade Foundation",
      logo = Logo(
        src = "https://static.theguardian.com/commercial/sponsor/sustainable/series/spotlight-commodities/logo.png",
        width = None,
        height = None,
        link = "http://www.fairtrade.org.uk/"
      ),
      logoForDarkBackground = None
    ))
  }

  it should "give AU edition branding for content in AU edition with a branded tag in AU edition" in {
    val item = getEditionTargetedTagBrandedItem
    val branding = BrandingFinder.findItemBranding(item, edition = "au")
    branding.value should be(Branding(
      brandingType = Sponsored,
      sponsor = "Optus Premier League: the view from Australia podcast",
      logo = Logo(
        src = "https://static.theguardian.com/commercial/sponsor/14/Sep/2016/6076c5fb-a3fd-49b8-Optuslogo.jpeg",
        width = None,
        height = None,
        link = "https://ad.doubleclick.net/ddm/clk/307408916"
      ),
      logoForDarkBackground = None
    ))
  }

  it should "give no branding for content in UK edition with a branded tag in AU edition" in {
    val item = getEditionTargetedTagBrandedItem
    val branding = BrandingFinder.findItemBranding(item, edition = "uk")
    branding should be(None)
  }

  it should "give branding for content published after the threshold date" in {
    val item = getAfterDateTargetedTagBrandedItem
    val branding = BrandingFinder.findItemBranding(item, edition = "uk")
    branding.value should be(Branding(
      brandingType = Sponsored,
      sponsor = "ING DIRECT",
      logo = Logo(
        src = "https://static.theguardian.com/commercial/sponsor/06/Oct/2016/d767ce82-0525-ING_dreamstarter_140.png",
        width = None,
        height = None,
        link = "https://www.campaigns.ingdirect.com.au/dreamstarter"
      ),
      logoForDarkBackground = None
    ))
  }

  it should "give no branding for content published before the threshold date" in {
    val item = getBeforeDateTargetedTagBrandedItem
    val branding = BrandingFinder.findItemBranding(item, edition = "uk")
    branding should be(None)
  }


  "findItemSetBranding" should "give branding if all items in set have same branding" is pending

  "findSectionBranding" should "give section branding for a branded section result" is pending

  "findTagBranding" should "give tag branding for a branded tag result" is pending
}
