package com.gu.commercial.branding

import com.gu.contentapi.client.model.v1.{Content, Section, Tag}
import com.gu.facia.api.models.CollectionConfig
import com.gu.facia.client.models.Branded

object BrandingFinder {

  /**
    * Finds branding of a single content item, tag or section.
    *
    * @param brandable Content item with <code>section</code>, <code>isInappropriateForSponsorship</code> field
    *                  and all <code>tags</code> populated, or Tag, or Section
    * @param edition   in lowercase String format eg. <code>uk</code>
    */
  def findBranding[T: Brandable](edition: String)(brandable: T): Option[Branding] =
    implicitly[Brandable[T]].findBranding(edition)(brandable)

  /**
    * Finds branding of a set of content items, tags and sections in a container.
    *
    * @param config  Configuration of container holding this content
    * @param items   Content items with <code>section</code>, <code>isInappropriateForSponsorship</code> field
    *                and all <code>tags</code> populated
    * @param edition in lowercase String format eg. <code>uk</code>
    */
  def findContainerBranding(
    config: CollectionConfig,
    edition: String,
    items: Set[Content],
    tags: Set[Tag] = Set.empty,
    sections: Set[Section] = Set.empty
  ): Option[ContainerBranding] = {
    def toBranding[T: Brandable](brandable: T) = findBranding(edition)(brandable)
    findContainerBranding(
      isConfiguredForBranding = config.metadata.exists(_.contains(Branded)),
      optBrandings = items.map(toBranding(_)) ++ tags.map(toBranding(_)) ++ sections.map(toBranding(_))
    )
  }

  private def findCommonBranding(brandings: Set[Branding]): Option[Branding] =
    if (brandings.size == 1) brandings.headOption else None

  private def areAllPaidContent(brandings: Set[Branding]): Boolean =
    brandings.forall(_.brandingType == PaidContent)

  def findContainerBranding(
    isConfiguredForBranding: Boolean,
    optBrandings: Set[Option[Branding]]
  ): Option[ContainerBranding] =
    if (isConfiguredForBranding && optBrandings.nonEmpty && optBrandings.forall(_.nonEmpty)) {
      val brandings = optBrandings.flatten
      findCommonBranding(brandings) orElse {
        if (areAllPaidContent(brandings)) Some(PaidMultiSponsorBranding)
        else None
      }
    } else None
}
