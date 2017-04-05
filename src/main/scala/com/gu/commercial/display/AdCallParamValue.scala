package com.gu.commercial.display

import com.gu.commercial.branding.Branding
import com.gu.commercial.display.Surge.bucket
import com.gu.contentapi.client.model.v1.TagType._
import com.gu.contentapi.client.model.v1.{Content, Tag}

trait AdCallParamValue {

  private val ukNewsKeywordId = "uk/uk"

  def nonEmpty: Boolean

  def targetValue(id: String): String = {
    def takeFinalSegment(s: String) = s.takeRight(s.length - s.lastIndexOf('/') - 1)
    if (id == ukNewsKeywordId) id
    else takeFinalSegment(id)
  }
}

trait SingleValue extends AdCallParamValue {
  def raw: String
  lazy val toCleanString = Cleaner.cleanValue(raw)
  override lazy val nonEmpty = toCleanString.nonEmpty
}

trait MultipleValues extends AdCallParamValue {
  def raw: Set[String]
  lazy val toCleanStrings = raw map Cleaner.cleanValue
  override lazy val nonEmpty = toCleanStrings.nonEmpty && toCleanStrings.forall(_.nonEmpty)
}

trait TagValues extends MultipleValues {
  def item: Content
  def condition(tag: Tag): Boolean
  override lazy val raw: Set[String] = for (tag <- item.tags.toSet if condition(tag)) yield targetValue(tag.id)
}

case class Authors(item: Content) extends TagValues {
  override def condition(tag: Tag) = tag.`type` == Contributor
}

case class Blogs(item: Content) extends TagValues {
  override def condition(tag: Tag) = tag.`type` == Blog
}

case class BrandingValue(branding: Option[Branding]) extends SingleValue {
  override val raw = branding.map(_.brandingType.name.take(1)) getOrElse ""
}

case class ContentType(item: Content) extends SingleValue {
  override val raw = item.`type`.name
}

case class EditionValue(edition: String) extends SingleValue {
  override val raw = edition.take(3)
}

case class ItemValue(path: String) extends SingleValue {
  override val raw = path.stripPrefix("/")
}

case class KeywordsAndPaidTopics(item: Content) extends TagValues {
  override def condition(tag: Tag) = tag.`type` == Keyword || tag.paidContentType.contains("Topic")
}

case class ObserverValue(item: Content) extends SingleValue {
  override val raw = if (item.tags.exists(_.id == "publication/theobserver")) "t" else ""
}

case class PathValue(itemId: String) extends SingleValue {
  override val raw = s"/$itemId"
}

case class RawStringValue(s: String) extends SingleValue {
  override val raw = s
}

case class SeriesAndPaidSeries(item: Content) extends TagValues {
  override def condition(tag: Tag) = tag.`type` == Series || tag.paidContentType.contains("Series")
}

case class StringValue(s: String) extends SingleValue {
  override val raw = targetValue(s)
}

case class SurgeBuckets(item: Content, surgeLookupService: SurgeLookupService) extends MultipleValues {
  override val raw: Set[String] = bucket(surgeLookupService.pageViewsPerMinute(item.id)).map(_.toString).toSet
}

case class Tones(item: Content) extends TagValues {
  override def condition(tag: Tag) = tag.`type` == Tone
}
