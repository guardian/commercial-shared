package com.gu.commercial.display

import com.gu.commercial.branding.{Brandable, BrandingFinder}
import com.gu.commercial.display.Surge.bucket
import com.gu.contentapi.client.model.v1.TagType._
import com.gu.contentapi.client.model.v1.{Content, Section, Tag}
import play.api.libs.json.{Format, JsSuccess, JsValue, Json}

sealed trait AdTargetParam {
  def name: String
  def value: AdTargetParamValue
}

object AdTargetParam {

  def toMap(params: Set[AdTargetParam]): Map[String, AdTargetParamValue] =
    params.map { param =>
      param.name -> param.value
    }.toMap

  implicit val format: Format[AdTargetParam] = new Format[AdTargetParam] {
    override def writes(o: AdTargetParam) = Json.obj(
      "name"  -> o.name,
      "value" -> o.value
    )
    override def reads(json: JsValue) = JsSuccess {
      val name = (json \ "name").asOpt[String]
      name match {
        case Some(AuthorParam.name)      => AuthorParam((json \ "value").as[MultipleValues])
        case Some(BlogParam.name)        => BlogParam((json \ "value").as[MultipleValues])
        case Some(BrandingParam.name)    => BrandingParam((json \ "value").as[SingleValue])
        case Some(ContentTypeParam.name) => ContentTypeParam((json \ "value").as[SingleValue])
        case Some(EditionParam.name)     => EditionParam((json \ "value").as[SingleValue])
        case Some(KeywordParam.name)     => KeywordParam((json \ "value").as[MultipleValues])
        case Some(ObserverParam.name)    => ObserverParam((json \ "value").as[SingleValue])
        case Some(PathParam.name)        => PathParam((json \ "value").as[SingleValue])
        case Some(PlatformParam.name)    => PlatformParam((json \ "value").as[SingleValue])
        case Some(SeriesParam.name)      => SeriesParam((json \ "value").as[MultipleValues])
        case Some(ShortUrlParam.name)    => ShortUrlParam((json \ "value").as[SingleValue])
        case Some(SurgeLevelParam.name)  => SurgeLevelParam((json \ "value").as[MultipleValues])
        case Some(ToneParam.name)        => ToneParam((json \ "value").as[MultipleValues])
        case _                           => UnknownParam
      }
    }
  }
}

case class AuthorParam(value: MultipleValues) extends AdTargetParam {
  override val name = AuthorParam.name
}

object AuthorParam {
  val name = "co"

  def from(item: Content): Option[AuthorParam] = MultipleValues.fromTagsOfType(item, Contributor) map (AuthorParam(_))

  def from(tag: Tag): Option[AuthorParam] =
    if (tag.`type` == Contributor) MultipleValues.fromItemId(tag.id) map (AuthorParam(_))
    else None
}

case class BlogParam(value: MultipleValues) extends AdTargetParam {
  override val name = BlogParam.name
}

object BlogParam {
  val name = "bl"

  def from(item: Content): Option[BlogParam] = MultipleValues.fromTagsOfType(item, Blog) map (BlogParam(_))

  def from(tag: Tag): Option[BlogParam] =
    if (tag.`type` == Blog) MultipleValues.fromItemId(tag.id) map (BlogParam(_))
    else None
}

case class BrandingParam(value: SingleValue) extends AdTargetParam {
  override val name = BrandingParam.name
}

object BrandingParam {
  val name = "br"

  def from[T: Brandable](brandable: T, editionId: String): Option[BrandingParam] = {
    BrandingFinder.findBranding(editionId)(brandable) map { branding =>
      val brandingType = branding.brandingType.name.take(1)
      BrandingParam(SingleValue(brandingType))
    }
  }
}

case class ContentTypeParam(value: SingleValue) extends AdTargetParam {
  override val name = ContentTypeParam.name
}

object ContentTypeParam {
  val name = "ct"

  def apply(typeName: String): ContentTypeParam     = ContentTypeParam(SingleValue(typeName))
  def from(item: Content): Option[ContentTypeParam] = SingleValue.fromRaw(item.`type`.name) map (ContentTypeParam(_))
}

case class EditionParam(value: SingleValue) extends AdTargetParam {
  override val name = EditionParam.name
}

object EditionParam {
  val name = "edition"

  def from(editionId: String): Option[EditionParam] = SingleValue.fromRaw(editionId.take(3)) map (EditionParam(_))
}

case class KeywordParam(value: MultipleValues) extends AdTargetParam {
  override val name = KeywordParam.name
}

object KeywordParam {
  val name = "k"

  def from(item: Content): Option[KeywordParam] =
    MultipleValues.fromTags(item) { tag =>
      tag.`type` == Keyword || tag.paidContentType.contains("Topic")
    } map (KeywordParam(_))

  def fromItemId(id: String): Option[KeywordParam] = MultipleValues.fromItemId(id) map (KeywordParam(_))

  def from(section: Section): Option[KeywordParam] = MultipleValues.fromItemId(section.id) map (KeywordParam(_))

  def from(tag: Tag): Option[KeywordParam] =
    if (tag.`type` == Keyword) MultipleValues.fromItemId(tag.id) map (KeywordParam(_))
    else None

  def fromPath(path: String): Option[KeywordParam] =
    MultipleValues.fromRawValues(Set(path.stripPrefix("/"))) map (KeywordParam(_))
}

case class ObserverParam(value: SingleValue) extends AdTargetParam {
  override val name = ObserverParam.name
}

object ObserverParam {
  val name = "ob"

  def from(item: Content): Option[ObserverParam] =
    SingleValue.fromRaw(if (item.tags.exists(_.id == "publication/theobserver")) "t" else "") map (ObserverParam(_))
}

case class PathParam(value: SingleValue) extends AdTargetParam {
  override val name = PathParam.name
}

object PathParam {
  val name = "url"

  def apply(value: String): PathParam           = PathParam(SingleValue(value))
  def fromItemId(id: String): Option[PathParam] = SingleValue.fromRaw(s"/${id.stripPrefix("/")}") map (PathParam(_))
  def from(item: Content): Option[PathParam]    = fromItemId(item.id)
  def from(section: Section): Option[PathParam] = fromItemId(section.id)
  def from(tag: Tag): Option[PathParam]         = fromItemId(tag.id)
}

case class PlatformParam(value: SingleValue) extends AdTargetParam {
  override val name = PlatformParam.name
}

object PlatformParam {
  val name = "p"

  def apply(value: String): PlatformParam = PlatformParam(SingleValue(value))
}

case class SeriesParam(value: MultipleValues) extends AdTargetParam {
  override val name = SeriesParam.name
}

object SeriesParam {
  val name = "se"

  def from(item: Content): Option[SeriesParam] =
    MultipleValues.fromTags(item) { tag =>
      tag.`type` == Series || tag.paidContentType.contains("Series")
    } map (SeriesParam(_))

  def from(tag: Tag): Option[SeriesParam] =
    if (tag.`type` == Series) MultipleValues.fromItemId(tag.id) map (SeriesParam(_))
    else None
}

case class ShortUrlParam(value: SingleValue) extends AdTargetParam {
  override val name = ShortUrlParam.name
}

object ShortUrlParam {
  val name = "sh"

  def from(item: Content): Option[ShortUrlParam] =
    SingleValue.fromRaw(item.fields.flatMap(_.shortUrl).getOrElse("")) map (ShortUrlParam(_))
}

case class SurgeLevelParam(value: MultipleValues) extends AdTargetParam {
  override val name = SurgeLevelParam.name
}

object SurgeLevelParam {
  val name = "su"

  def from(item: Content, surgeLookup: SurgeLookupService): Option[SurgeLevelParam] = {
    val surgeLevels = bucket(surgeLookup.pageViewsPerMinute(item.id)).map(_.toString).toSet
    MultipleValues.fromRawValues(surgeLevels) map (SurgeLevelParam(_))
  }
}

case class ToneParam(value: MultipleValues) extends AdTargetParam {
  override val name = ToneParam.name
}

object ToneParam {
  val name = "tn"

  def from(item: Content): Option[ToneParam] = MultipleValues.fromTagsOfType(item, Tone) map (ToneParam(_))

  def from(tag: Tag): Option[ToneParam] =
    if (tag.`type` == Tone) MultipleValues.fromItemId(tag.id) map (ToneParam(_))
    else None
}

case object UnknownParam extends AdTargetParam {
  override def name  = ""
  override def value = SingleValue.empty
}
