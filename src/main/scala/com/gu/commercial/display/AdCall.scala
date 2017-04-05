package com.gu.commercial.display

import com.gu.commercial.branding.BrandingFinder
import com.gu.contentapi.client.model.v1.TagType._
import com.gu.contentapi.client.model.v1.{Content, Section, Tag}

class AdCall(platform: String, surgeLookupService: SurgeLookupService) {

  /**
    * Content page.
    *
    * @param item    Content item with <code>section</code> and all <code>tags</code> populated
    * @param edition eg. <code>uk</code>
    * @return Contextual targeting parameters to pass in display ad call
    */
  def pageLevelTargetingForContentPage(item: Content)(
    implicit edition: String): Map[AdCallParamKey, AdCallParamValue] =
    for {
      (name, value) <- Map[AdCallParamKey, AdCallParamValue](
        AuthorKey -> Authors(item),
        BlogKey -> Blogs(item),
        BrandingKey -> BrandingValue(BrandingFinder.findBranding(item, edition)),
        ContentTypeKey -> ContentType(item),
        EditionKey -> EditionValue(edition),
        KeywordKey -> KeywordsAndPaidTopics(item),
        ObserverKey -> ObserverValue(item),
        PathKey -> PathValue(item.id),
        PlatformKey -> RawStringValue(platform),
        SeriesKey -> SeriesAndPaidSeries(item),
        SurgeLevelKey -> SurgeBuckets(item, surgeLookupService),
        ToneKey -> Tones(item)
      ) if value.nonEmpty
    } yield (name, value)

  def pageLevelTargetingForSectionFront(section: Section)(
    implicit edition: String): Map[AdCallParamKey, AdCallParamValue] =
    for {
      (name, value) <- Map[AdCallParamKey, AdCallParamValue](
        BrandingKey -> BrandingValue(BrandingFinder.findBranding(section, edition)),
        ContentTypeKey -> StringValue("section"),
        EditionKey -> EditionValue(edition),
        KeywordKey -> StringValue(section.id),
        PathKey -> PathValue(section.id),
        PlatformKey -> RawStringValue(platform)
      ) if value.nonEmpty
    } yield (name, value)

  def pageLevelTargetingForTagPage(tag: Tag)(implicit edition: String): Map[AdCallParamKey, AdCallParamValue] = {

    val tagParam = {
      val tagType = tag.`type` match {
        case Blog => Some(BlogKey)
        case Contributor => Some(AuthorKey)
        case Keyword => Some(KeywordKey)
        case Series => Some(SeriesKey)
        case Tone => Some(ToneKey)
        case _ => None
      }
      tagType map (_ -> StringValue(tag.id))
    }

    val params = for {
      (key, value) <- Map[AdCallParamKey, AdCallParamValue](
        BrandingKey -> BrandingValue(BrandingFinder.findBranding(tag, edition)),
        ContentTypeKey -> StringValue("tag"),
        EditionKey -> EditionValue(edition),
        PathKey -> PathValue(tag.id),
        PlatformKey -> RawStringValue(platform)
      ) if value.nonEmpty
    } yield (key, value)
    tagParam map (p => params + p) getOrElse params
  }

  def pageLevelTargetingForNetworkFront(networkFrontPath: String)(
    implicit edition: String): Map[AdCallParamKey, AdCallParamValue] =
    for {
      (name, value) <- Map[AdCallParamKey, AdCallParamValue](
        ContentTypeKey -> StringValue("network-front"),
        EditionKey -> EditionValue(edition),
        KeywordKey -> ItemValue(networkFrontPath),
        PathKey -> RawStringValue(networkFrontPath),
        PlatformKey -> RawStringValue(platform)
      ) if value.nonEmpty
    } yield (name, value)

  def pageLevelTargetingForFrontUnknownToCapi(frontId: String)(
    implicit edition: String): Map[AdCallParamKey, AdCallParamValue] =
    Map(
      ContentTypeKey -> StringValue("section"),
      EditionKey -> EditionValue(edition),
      KeywordKey -> StringValue(frontId),
      PathKey -> PathValue(frontId),
      PlatformKey -> RawStringValue(platform)
    )
}
