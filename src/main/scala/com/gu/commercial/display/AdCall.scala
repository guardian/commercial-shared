package com.gu.commercial.display

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
        AuthorKey      -> MultipleValues.authors(item),
        BlogKey        -> MultipleValues.blogs(item),
        BrandingKey    -> SingleValue.branding(item, edition),
        ContentTypeKey -> SingleValue.contentType(item),
        EditionKey     -> SingleValue.edition(edition),
        KeywordKey     -> MultipleValues.keywordsAndPaidTopics(item),
        ObserverKey    -> SingleValue.observer(item),
        PathKey        -> SingleValue.path(item.id),
        PlatformKey    -> SingleValue(platform),
        SeriesKey      -> MultipleValues.seriesAndPaidSeries(item),
        ShortUrlKey    -> SingleValue.shortUrl(item),
        SurgeLevelKey  -> MultipleValues.surgeBuckets(item, surgeLookupService),
        ToneKey        -> MultipleValues.tones(item)
      ) if value.nonEmpty
    } yield (name, value)

  def pageLevelTargetingForSectionFront(edition: String)(section: Section): Map[AdCallParamKey, AdCallParamValue] =
    for {
      (name, value) <- Map[AdCallParamKey, AdCallParamValue](
        BrandingKey    -> SingleValue.branding(section, edition),
        ContentTypeKey -> SingleValue("section"),
        EditionKey     -> SingleValue.edition(edition),
        KeywordKey     -> SingleValue.targetValue(section.id),
        PathKey        -> SingleValue.path(section.id),
        PlatformKey    -> SingleValue(platform)
      ) if value.nonEmpty
    } yield (name, value)

  def pageLevelTargetingForTagPage(edition: String)(tag: Tag): Map[AdCallParamKey, AdCallParamValue] = {

    val tagParam = {
      val tagType = tag.`type` match {
        case Blog        => Some(BlogKey)
        case Contributor => Some(AuthorKey)
        case Keyword     => Some(KeywordKey)
        case Series      => Some(SeriesKey)
        case Tone        => Some(ToneKey)
        case _           => None
      }
      tagType map (_ -> SingleValue.targetValue(tag.id))
    }

    val params = for {
      (key, value) <- Map[AdCallParamKey, AdCallParamValue](
        BrandingKey    -> SingleValue.branding(tag, edition),
        ContentTypeKey -> SingleValue("tag"),
        EditionKey     -> SingleValue.edition(edition),
        PathKey        -> SingleValue.path(tag.id),
        PlatformKey    -> SingleValue(platform)
      ) if value.nonEmpty
    } yield (key, value)
    tagParam map (p => params + p) getOrElse params
  }

  def pageLevelTargetingForNetworkFront(edition: String)(
    networkFrontPath: String): Map[AdCallParamKey, AdCallParamValue] =
    for {
      (name, value) <- Map[AdCallParamKey, AdCallParamValue](
        ContentTypeKey -> SingleValue("network-front"),
        EditionKey     -> SingleValue.edition(edition),
        KeywordKey     -> SingleValue.id(networkFrontPath),
        PathKey        -> SingleValue.path(networkFrontPath),
        PlatformKey    -> SingleValue(platform)
      ) if value.nonEmpty
    } yield (name, value)

  def pageLevelTargetingForFrontUnknownToCapi(edition: String)(
    frontId: String): Map[AdCallParamKey, AdCallParamValue] =
    Map(
      ContentTypeKey -> SingleValue("section"),
      EditionKey     -> SingleValue.edition(edition),
      KeywordKey     -> SingleValue.targetValue(frontId),
      PathKey        -> SingleValue.path(frontId),
      PlatformKey    -> SingleValue(platform)
    )
}
