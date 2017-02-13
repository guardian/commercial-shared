package com.gu.commercial.display

import com.gu.commercial.branding.{Branding, BrandingFinder}
import com.gu.contentapi.client.model.v1.TagType._
import com.gu.contentapi.client.model.v1.{Content, Section, Tag}

class AdCall(platform: String) {

  private val ukNewsKeywordId = "uk/uk"

  private def targetValue(id: String): String = {
    def takeFinalSegment(s: String) = s.takeRight(s.length - s.lastIndexOf('/') - 1)
    if (id == ukNewsKeywordId) id
    else takeFinalSegment(id)
  }

  private def targetValue(tag: Tag): String = targetValue(tag.id)

  private def toBrandingType(b: Option[Branding]) = b.map(_.brandingType.name.take(1)) getOrElse ""

  /**
    * Content page.
    *
    * @param item    Content item with <code>section</code> and all <code>tags</code> populated
    * @param edition eg. <code>uk</code>
    * @return Contextual targeting parameters to pass in display ad call
    */
  def pageLevelContextTargeting(item: Content, edition: String): Map[AdCallParamKey, String] = {

    def tagValues(p: Tag => Boolean) = for (tag <- item.tags if p(tag)) yield targetValue(tag)

    def csv(values: Seq[String]) = values.mkString(",")

    val authors = tagValues(_.`type` == Contributor)
    val blogs = tagValues(_.`type` == Blog)
    val brandingType = toBrandingType(BrandingFinder.findBranding(item, edition))
    val contentType = item.`type`.name.toLowerCase
    val isObserver = if (tagValues(_.`type` == Publication).contains("theobserver")) "t" else ""
    val keywords = tagValues(_.`type` == Keyword)
    val paidSeries = tagValues(_.paidContentType.contains("Series"))
    val paidTopics = tagValues(_.paidContentType.contains("Topic"))
    val path = s"/${item.id}"
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
        PathKey -> path,
        PlatformKey -> platform,
        SeriesKey -> csv(series ++ paidSeries),
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
      PathKey -> s"/${ section.id }",
      PlatformKey -> platform
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
        PathKey -> s"/${ tag.id }",
        PlatformKey -> platform
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
      KeywordKey -> networkFrontPath.stripPrefix("/"),
      PathKey -> networkFrontPath,
      PlatformKey -> platform
    ) if value.nonEmpty
  } yield (name, value)
}
