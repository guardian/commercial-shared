package com.gu.commercial.branding

import com.gu.commercial.branding.BrandingFinder.findBranding
import com.gu.commercial.branding.TestModel.{StubItem, StubSection, StubTag}
import com.gu.contentapi.client.model.v1.{Content, Section, Tag}
import com.gu.facia.api.models.CollectionConfig
import com.gu.facia.client.models.Branded
import net.liftweb.json
import net.liftweb.json.JsonAST.{JField, JValue}
import org.scalatest.{FlatSpec, Matchers, OptionValues}

import scala.io.Source

class BrandingFinderSpec extends FlatSpec with Matchers with OptionValues {

  private implicit val jsonFormats = json.DefaultFormats

  private def getJson(fileName: String): JValue =
    json.parse(Source.fromURL(getClass.getResource(s"/$fileName")).mkString)

  private def getContentItem(fileName: String): Content =
    getJson(fileName).transformField {
      case JField("sponsorshipType", v) => JField("sponsorshipTypeName", v)
      case JField("webPublicationDate", v) => JField("publicationDateText", v)
      case JField("publishedSince", v) => JField("publishedSinceText", v)
      case JField("isInappropriateForSponsorship", v) => JField("isInappropriateForSponsorshipText", v)
    }.extract[StubItem]

  private def getSection(fileName: String): Section =
    getJson(fileName).transformField {
      case JField("sponsorshipType", v) => JField("sponsorshipTypeName", v)
    }.extract[StubSection]

  private def getTag(fileName: String): Tag =
    getJson(fileName).transformField {
      case JField("sponsorshipType", v) => JField("sponsorshipTypeName", v)
    }.extract[StubTag]

  private def getTagBrandedItem = getContentItem("TagBrandedContent.json")
  private def getMultipleTagBrandedItem = getContentItem("TagBrandedContent-MultipleBrands.json")
  private def getSectionBrandedItem = getContentItem("SectionBrandedContent.json")
  private def getSectionAndTagBrandedItem = getContentItem("SectionAndTagBrandedContent.json")
  private def getEditionTargetedTagBrandedItem = getContentItem("EditionTargetedTagBrandedContent.json")
  private def getBeforeDateTargetedTagBrandedItem = getContentItem("BeforeDateTargetedTagBrandedContent.json")
  private def getAfterDateTargetedTagBrandedItem = getContentItem("AfterDateTargetedTagBrandedContent.json")
  private def getInappropriateItem = getContentItem("InappropriateContent.json")

  private def getBrandedSection = getSection("BrandedSection.json")

  private def getBrandedTag = getTag("BrandedTag.json")

  private val brandedContainerConfig = CollectionConfig.empty.copy(metadata = Some(List(Branded)))

  "findBranding: item" should "give branding of tag for content with a single branded tag" in {
    val item = getTagBrandedItem
    val branding = findBranding(item, edition = "uk")
    branding.value should be(Branding(
      brandingType = Sponsored,
      sponsorName = "Fairtrade Foundation",
      logo = Logo(
        src = "https://static.theguardian.com/commercial/sponsor/sustainable/series/spotlight-commodities/logo.png",
        dimensions = None,
      link = "http://www.fairtrade.org.uk/"
      ),
      logoForDarkBackground = None
    ))
  }

  it should "give branding of first matching branded tag for content with multiple branded tags" in {
    val item = getMultipleTagBrandedItem
    val branding = findBranding(item, edition = "uk")
    branding.value should be(Branding(
      brandingType = Sponsored,
      sponsorName = "Fairtrade Foundation",
      logo = Logo(
        src = "https://static.theguardian.com/commercial/sponsor/sustainable/series/spotlight-commodities/logo.png",
        dimensions = None,
        link = "http://www.fairtrade.org.uk/"
      ),
      logoForDarkBackground = None
    ))
  }

  it should "give section branding for content in a branded section" in {
    val item = getSectionBrandedItem
    val branding = findBranding(item, edition = "uk")
    branding.value should be(Branding(
      brandingType = Sponsored,
      sponsorName = "Rockefeller Foundation",
      logo = Logo(
        src = "https://static.theguardian.com/commercial/sponsor/cities/cities/logo.png",
        dimensions = Some(Dimensions(width = 140, height = 37)),
        link = "http://www.100resilientcities.org/"
      ),
      logoForDarkBackground = Some(Logo(
        src = "https://static.theguardian.com/commercial/sponsor/19/Oct/2016/4369caea-6271-4ddf-ad67-Rock_white.png",
        dimensions = Some(Dimensions(width = 140, height = 47)),
        link = "http://www.100resilientcities.org/"
      ))
    ))
  }

  it should "give branding of tag for content in a branded section and with a branded tag" in {
    val item = getSectionAndTagBrandedItem
    val branding = findBranding(item, edition = "uk")
    branding.value should be(Branding(
      brandingType = Sponsored,
      sponsorName = "Fairtrade Foundation",
      logo = Logo(
        src = "https://static.theguardian.com/commercial/sponsor/sustainable/series/spotlight-commodities/logo.png",
        dimensions = None,
        link = "http://www.fairtrade.org.uk/"
      ),
      logoForDarkBackground = None
    ))
  }

  it should "give AU edition branding for content in AU edition with a branded tag in AU edition" in {
    val item = getEditionTargetedTagBrandedItem
    val branding = findBranding(item, edition = "au")
    branding.value should be(Branding(
      brandingType = Sponsored,
      sponsorName = "Optus Premier League: the view from Australia podcast",
      logo = Logo(
        src = "https://static.theguardian.com/commercial/sponsor/14/Sep/2016/6076c5fb-a3fd-49b8-Optuslogo.jpeg",
        dimensions = None,
        link = "https://ad.doubleclick.net/ddm/clk/307408916"
      ),
      logoForDarkBackground = None
    ))
  }

  it should "give no branding for content in UK edition with a branded tag in AU edition" in {
    val item = getEditionTargetedTagBrandedItem
    val branding = findBranding(item, edition = "uk")
    branding should be(None)
  }

  it should "give branding for content published after the threshold date" in {
    val item = getAfterDateTargetedTagBrandedItem
    val branding = findBranding(item, edition = "uk")
    branding.value should be(Branding(
      brandingType = Sponsored,
      sponsorName = "ING DIRECT",
      logo = Logo(
        src = "https://static.theguardian.com/commercial/sponsor/06/Oct/2016/d767ce82-0525-ING_dreamstarter_140.png",
        dimensions = Some(Dimensions(width = 140, height = 58)),
        link = "https://www.campaigns.ingdirect.com.au/dreamstarter"
      ),
      logoForDarkBackground = None
    ))
  }

  it should "give no branding for content published before the threshold date" in {
    val item = getBeforeDateTargetedTagBrandedItem
    val branding = findBranding(item, edition = "uk")
    branding should be(None)
  }

  it should "give no branding for content with the isInappropriateForSponsorship flag" in {
    val item = getInappropriateItem
    val branding = findBranding(item, edition = "uk")
    branding should be(None)
  }

  "findBranding: items" should "give branding if all items in set have same branding" in {
    val items = Set(
      getTagBrandedItem,
      getMultipleTagBrandedItem
    )
    val branding = findBranding(brandedContainerConfig, items, edition = "uk")
    branding.value should be(Branding(
      brandingType = Sponsored,
      sponsorName = "Fairtrade Foundation",
      logo = Logo(
        src = "https://static.theguardian.com/commercial/sponsor/sustainable/series/spotlight-commodities/logo.png",
        dimensions = None,
        link = "http://www.fairtrade.org.uk/"
      ),
      logoForDarkBackground = None
    ))
  }

  it should "give no branding if any item in set has different branding" in {
    val items = Set(
      getTagBrandedItem,
      getSectionBrandedItem
    )
    val branding = findBranding(brandedContainerConfig, items, edition = "uk")
    branding should be(None)
  }

  it should "give no branding for an empty set" in {
    val branding = findBranding(brandedContainerConfig, content = Set.empty, edition = "uk")
    branding should be(None)
  }

  it should "give no branding for a container without branded config" in {
    val items = Set(
      getTagBrandedItem,
      getMultipleTagBrandedItem
    )
    val branding = findBranding(CollectionConfig.empty, items, edition = "uk")
    branding should be(None)
  }

  "findBranding: section" should "give section branding for a branded section result" in {
    val section = getBrandedSection
    val branding = findBranding(section, edition = "uk")
    branding.value should be(Branding(
      brandingType = Sponsored,
      sponsorName = "Rockefeller Foundation",
      logo = Logo(
        src = "https://static.theguardian.com/commercial/sponsor/cities/cities/logo.png",
        dimensions = Some(Dimensions(width = 140, height = 37)),
        link = "http://www.100resilientcities.org/"
      ),
      logoForDarkBackground = Some(Logo(
        src = "https://static.theguardian.com/commercial/sponsor/19/Oct/2016/4369caea-6271-4ddf-ad67-Rock_white.png",
        dimensions = Some(Dimensions(width = 140, height = 47)),
        link = "http://www.100resilientcities.org/"
      ))
    ))
  }

  "findBranding: tag" should "give tag branding for a branded tag result" in {
    val tag = getBrandedTag
    val branding = findBranding(tag, edition = "uk")
    branding.value should be(Branding(
      brandingType = Sponsored,
      sponsorName = "Fairtrade Foundation",
      logo = Logo(
        src = "https://static.theguardian.com/commercial/sponsor/sustainable/series/spotlight-commodities/logo.png",
        dimensions = None,
        link = "http://www.fairtrade.org.uk/"
      ),
      logoForDarkBackground = None
    ))
  }
}
