package com.gu.commercial.display

import com.gu.contentapi.client.model.v1.{Content, Tag, TagType}
import play.api.libs.json._

sealed trait AdTargetParamValue

object AdTargetParamValue {

  implicit val writes: Writes[AdTargetParamValue] = new Writes[AdTargetParamValue] {
    override def writes(o: AdTargetParamValue) = o match {
      case SingleValue(value)     => Json.toJson(value)
      case MultipleValues(values) => Json.toJson(values)
    }
  }
}

case class SingleValue(value: String) extends AdTargetParamValue

object SingleValue {

  implicit val reads: Reads[SingleValue] = new Reads[SingleValue] {
    override def reads(json: JsValue) =
      JsSuccess(json match {
        case JsString(v) => SingleValue(v)
        case _           => empty
      })
  }

  val empty = SingleValue("")

  def fromRaw(raw: String): Option[SingleValue] = {
    val clean = Cleaner.cleanValue(raw)
    if (clean.isEmpty) None else Some(SingleValue(clean))
  }
}

case class MultipleValues(values: Set[String]) extends AdTargetParamValue

object MultipleValues {

  implicit val reads: Reads[MultipleValues] = new Reads[MultipleValues] {
    def reads(json: JsValue) =
      JsSuccess(json match {
        case JsArray(vs) => MultipleValues(vs.collect { case JsString(v) => v }.toSet)
        case _           => empty
      })
  }

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
