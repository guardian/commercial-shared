package com.gu.commercial.display

import com.gu.commercial.branding.{Branding, BrandingFinder}
import com.gu.commercial.display.Surge.bucket
import com.gu.contentapi.client.model.v1.TagType._
import com.gu.contentapi.client.model.v1.{Content, Section, Tag}

class AdCall(platform: String, surgeLookupService: SurgeLookupService) {

  private val ukNewsKeywordId = "uk/uk"

  private def csv[A](values: Seq[A]) = values.mkString(",")

  private def toItemId(path: String) = path.stripPrefix("/")
  private def toPath(itemId: String) = s"/$itemId"

  private def targetValue(id: String): String = {
    def takeFinalSegment(s: String) = s.takeRight(s.length - s.lastIndexOf('/') - 1)
    if (id == ukNewsKeywordId) id
    else takeFinalSegment(id)
  }

  private def targetValue(tag: Tag): String = targetValue(tag.id)

  private def toBrandingType(b: Option[Branding]) = b.map(_.brandingType.name.take(1)) getOrElse ""

  private def surgeLevels(itemId: String) = csv(bucket(surgeLookupService.pageViewsPerMinute(itemId)))

  /**
    * Content page.
    *
    * @param item    Content item with <code>section</code> and all <code>tags</code> populated
    * @param edition eg. <code>uk</code>
    * @return Contextual targeting parameters to pass in display ad call
    */
  def pageLevelContextTargeting(item: Content, edition: String): Map[AdCallParamKey, String] = {

    def tagValues(p: Tag => Boolean) = for (tag <- item.tags if p(tag)) yield targetValue(tag)

    val authors = tagValues(_.`type` == Contributor)
    val blogs = tagValues(_.`type` == Blog)
    val brandingType = toBrandingType(BrandingFinder.findBranding(item, edition))
    val contentType = item.`type`.name.toLowerCase
    val isObserver = if (tagValues(_.`type` == Publication).contains("theobserver")) "t" else ""
    val keywords = tagValues(_.`type` == Keyword)
    val paidSeries = tagValues(_.paidContentType.contains("Series"))
    val paidTopics = tagValues(_.paidContentType.contains("Topic"))
    val series = tagValues(_.`type` == Series)
    val tones = tagValues(_.`type` == Tone)

    for {
      (name, value) <- Map[AdCallParamKey, String](
        AuthorKey -> csv(authors),
        BlogKey -> csv(blogs),
        BrandingKey -> brandingType,
        ContentTypeKey -> contentType,
        EditionKey -> edition.toLowerCase,
        KeywordKey -> csv(keywords ++ paidTopics),
        ObserverKey -> isObserver,
        PathKey -> toPath(item.id),
        PlatformKey -> platform,
        SeriesKey -> csv(series ++ paidSeries),
        SurgeLevelKey -> surgeLevels(item.id),
        ToneKey -> csv(tones)
      ) if value.nonEmpty
    } yield (name, value)
  }

  /**
    * Section front.
    */
  def pageLevelContextTargeting(section: Section, edition: String): Map[AdCallParamKey, String] = for {
    (name, value) <- Map[AdCallParamKey, String](
      BrandingKey -> toBrandingType(BrandingFinder.findBranding(section, edition)),
      ContentTypeKey -> "section",
      EditionKey -> edition.toLowerCase,
      KeywordKey -> targetValue(section.id),
      PathKey -> toPath(section.id),
      PlatformKey -> platform,
      SurgeLevelKey -> surgeLevels(section.id)
      ) if value.nonEmpty
    } yield (name, value)

  /**
    * Tag page.
    */
  def pageLevelContextTargeting(tag: Tag, edition: String): Map[AdCallParamKey, String] = {

    val tagParam = {
      val tagType = tag.`type` match {
        case Blog => Some(BlogKey)
        case Contributor => Some(AuthorKey)
        case Keyword => Some(KeywordKey)
        case Series => Some(SeriesKey)
        case Tone => Some(ToneKey)
        case _ => None
      }
      tagType map (_ -> targetValue(tag))
    }

    val params = for {
      (key, value) <- Map[AdCallParamKey, String](
        BrandingKey -> toBrandingType(BrandingFinder.findBranding(tag, edition)),
        ContentTypeKey -> "tag",
        EditionKey -> edition.toLowerCase,
        PathKey -> toPath(tag.id),
        PlatformKey -> platform,
        SurgeLevelKey -> surgeLevels(tag.id)
      ) if value.nonEmpty
    } yield (key, value)
    tagParam map (p => params + p) getOrElse params
  }

  /**
    * Network front.
    */
  def pageLevelContextTargeting(networkFrontPath: String, edition: String): Map[AdCallParamKey, String] = for {
    (name, value) <- Map[AdCallParamKey, String](
      ContentTypeKey -> "network-front",
      EditionKey -> edition.toLowerCase,
      KeywordKey -> toItemId(networkFrontPath),
      PathKey -> networkFrontPath,
      PlatformKey -> platform,
      SurgeLevelKey -> surgeLevels(toItemId(networkFrontPath))
    ) if value.nonEmpty
  } yield (name, value)
}
