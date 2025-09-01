package com.gu.commercial.display

import com.gu.contentapi.client.model.v1.{Content, Section, Tag}

class AdTargeter(platform: String, surgeLookupService: SurgeLookupService) {

  /**
    * Content page.
    *
    * @param editionId eg. <code>uk</code>
    * @param item      Content item with <code>section</code> and all <code>tags</code> populated
    * @return Contextual targeting parameters to pass in display ad call
    */
  def pageLevelTargetingForContentPage(editionId: String, renderingPlatform: Option[RenderingPlatformParam] = None)(item: Content): Set[AdTargetParam] =
    Set(
      AuthorParam.from(item),
      BlogParam.from(item),
      BrandingParam.from(item, editionId),
      ContentTypeParam.from(item),
      EditionParam.from(editionId),
      KeywordParam.from(item),
      ObserverParam.from(item),
      PathParam.from(item),
      Some(PlatformParam(platform)),
      renderingPlatform,
      SectionParam.from(item),
      SeriesParam.from(item),
      ShortUrlParam.from(item),
      SurgeLevelParam.from(item, surgeLookupService),
      ToneParam.from(item)
    ).flatten

  def pageLevelTargetingForSectionFront(editionId: String)(section: Section): Set[AdTargetParam] =
    Set(
      BrandingParam.from(section, editionId),
      Some(ContentTypeParam("section")),
      EditionParam.from(editionId),
      KeywordParam.from(section),
      PathParam.from(section),
      Some(PlatformParam(platform)),
      SectionParam.from(section)
    ).flatten

  def pageLevelTargetingForTagPage(editionId: String)(tag: Tag): Set[AdTargetParam] = {

    val tagParam =
      KeywordParam.from(tag) orElse
        SeriesParam.from(tag) orElse
        ToneParam.from(tag) orElse
        AuthorParam.from(tag) orElse
        BlogParam.from(tag)

    Set(
      tagParam,
      BrandingParam.from(tag, editionId),
      Some(ContentTypeParam("tag")),
      EditionParam.from(editionId),
      PathParam.from(tag),
      Some(PlatformParam(platform)),
      SectionParam.from(tag)
    ).flatten
  }

  def pageLevelTargetingForNetworkFront(editionId: String)(networkFrontPath: String): Set[AdTargetParam] =
    Set(
      Some(ContentTypeParam("network-front")),
      EditionParam.from(editionId),
      KeywordParam.fromPath(networkFrontPath),
      Some(PathParam(networkFrontPath)),
      Some(PlatformParam(platform)),
      SectionParam.fromPath(networkFrontPath)
    ).flatten

  def pageLevelTargetingForFrontUnknownToCapi(editionId: String)(frontId: String): Set[AdTargetParam] =
    Set(
      Some(ContentTypeParam("section")),
      EditionParam.from(editionId),
      KeywordParam.fromItemId(frontId),
      PathParam.fromItemId(frontId),
      Some(PlatformParam(platform)),
      SectionParam.fromPath(frontId)
    ).flatten
}
