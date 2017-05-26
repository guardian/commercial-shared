package com.gu.commercial.display

import com.gu.commercial.branding.{Brandable, BrandingFinder}
import com.gu.commercial.display.Surge.bucket
import com.gu.contentapi.client.model.v1.TagType._
import com.gu.contentapi.client.model.v1.{Content, Tag, TagType}

trait AdCallParamValue {
  def nonEmpty: Boolean
}

object AdCallParamValue {

  private val ukNewsKeywordId = "uk/uk"

  def targetValue(id: String): String = {
    def takeFinalSegment(s: String) = s.takeRight(s.length - s.lastIndexOf('/') - 1)
    if (id == ukNewsKeywordId) id
    else takeFinalSegment(id)
  }
}

case class SingleValue(value: String) extends AdCallParamValue {

  override val nonEmpty = value.nonEmpty

  @deprecated("Use value instead")
  val toCleanString = value
}

object SingleValue {

  private def fromRawValue(raw: String) = SingleValue(Cleaner.cleanValue(raw))

  def branding[T: Brandable](brandable: T, edition: String) =
    fromRawValue(BrandingFinder.findBranding(edition)(brandable).map(_.brandingType.name.take(1)) getOrElse "")

  def contentType(item: Content) = fromRawValue(item.`type`.name)

  def edition(edition: String) = fromRawValue(edition.take(3))

  def id(path: String) = fromRawValue(path.stripPrefix("/"))

  def observer(item: Content) = fromRawValue(if (item.tags.exists(_.id == "publication/theobserver")) "t" else "")

  def path(itemId: String) = fromRawValue(s"/${itemId.stripPrefix("/")}")

  def shortUrl(item: Content) = fromRawValue(item.fields.flatMap(_.shortUrl).getOrElse(""))

  def targetValue(tagId: String) = fromRawValue(AdCallParamValue.targetValue(tagId))
}

case class MultipleValues(values: Set[String]) extends AdCallParamValue {

  override val nonEmpty = values.nonEmpty && values.forall(_.nonEmpty)

  @deprecated("Use values instead")
  val toCleanStrings = values
}

object MultipleValues {

  private def fromRawValues(raw: Set[String]) = MultipleValues(raw map Cleaner.cleanValue)

  private def fromTags(item: Content)(p: Tag => Boolean) =
    fromRawValues(for (tag <- item.tags.toSet if p(tag)) yield AdCallParamValue.targetValue(tag.id))

  private def fromTagsOfType(item: Content, tagType: TagType) = fromTags(item)(_.`type` == tagType)

  def authors(item: Content) = fromTagsOfType(item, Contributor)

  def blogs(item: Content) = fromTagsOfType(item, Blog)

  def keywordsAndPaidTopics(item: Content) = fromTags(item) { tag =>
    tag.`type` == Keyword || tag.paidContentType.contains("Topic")
  }

  def seriesAndPaidSeries(item: Content) = fromTags(item) { tag =>
    tag.`type` == Series || tag.paidContentType.contains("Series")
  }

  def surgeBuckets(item: Content, surgeLookupService: SurgeLookupService) =
    fromRawValues(bucket(surgeLookupService.pageViewsPerMinute(item.id)).map(_.toString).toSet)

  def tones(item: Content) = fromTagsOfType(item, Tone)
}
