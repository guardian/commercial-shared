package com.gu.commercial.display

import com.gu.contentapi.client.model.v1.{Content, Tag, TagType}

sealed trait AdTargetParamValue

case class SingleValue(value: String) extends AdTargetParamValue

object SingleValue {

  val empty = SingleValue("")

  def fromRaw(raw: String): Option[SingleValue] = {
    val clean = Cleaner.cleanValue(raw)
    if (clean.isEmpty) None else Some(SingleValue(clean))
  }
}

case class MultipleValues(values: Set[String]) extends AdTargetParamValue

object MultipleValues {

  val empty = MultipleValues(Set.empty)

  private val ukNewsKeywordId = "uk/uk"

  private def targetValue(id: String): String = {
    def takeFinalSegment(s: String) = s.takeRight(s.length - s.lastIndexOf('/') - 1)
    if (id == ukNewsKeywordId) id
    else takeFinalSegment(id)
  }

  def fromRawValues(raw: Set[String]): Option[MultipleValues] = {
    val clean = raw map Cleaner.cleanValue
    if (clean.nonEmpty && clean.forall(_.nonEmpty)) Some(MultipleValues(clean)) else None
  }

  def fromItemId(id: String): Option[MultipleValues] = fromRawValues(Set(targetValue(id)))

  def fromTags(item: Content)(p: Tag => Boolean): Option[MultipleValues] =
    fromRawValues(for (tag <- item.tags.toSet if p(tag)) yield targetValue(tag.id))

  def fromTagsOfType(item: Content, tagType: TagType): Option[MultipleValues] =
    fromTags(item)(_.`type` == tagType)
}
