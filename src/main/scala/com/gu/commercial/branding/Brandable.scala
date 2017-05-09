package com.gu.commercial.branding

import com.gu.commercial.branding.SponsorshipHelper.{findSponsorshipFromSection, findSponsorshipFromTag}
import com.gu.contentapi.client.model.v1.{CapiDateTime, Content, Section, Tag}

trait Brandable[T] {
  def findBranding(edition: String)(brandable: T): Option[Branding]
}

object Brandable {

  private def findBrandingFromSection(
    section: Section,
    edition: String,
    publishedDate: Option[CapiDateTime]
  ): Option[Branding] =
    for (sponsorship <- findSponsorshipFromSection(section, edition, publishedDate))
      yield Branding.fromSponsorship(section.webTitle, campaignColour = None, sponsorship)

  private def findBrandingFromTag(
    tag: Tag,
    edition: String,
    publishedDate: Option[CapiDateTime]
  ): Option[Branding] =
    for (sponsorship <- findSponsorshipFromTag(tag, edition, publishedDate))
      yield Branding.fromSponsorship(tag.webTitle, campaignColour = tag.paidContentCampaignColour, sponsorship)

  implicit object BrandableContent extends Brandable[Content] {
    def findBranding(edition: String)(item: Content) = {

      val inappropriateForBranding = item.fields.exists(_.isInappropriateForSponsorship.contains(true))

      lazy val tagBranding = {
        val branding = for {
          tag <- item.tags.view
          branding <- findBrandingFromTag(tag, edition, item.webPublicationDate)
        } yield branding
        branding.headOption
      }

      lazy val sectionBranding = for {
        section <- item.section
        branding <- findBrandingFromSection(section, edition, item.webPublicationDate)
      } yield branding

      if (inappropriateForBranding) None
      else tagBranding orElse sectionBranding
    }
  }

  implicit object BrandableSection extends Brandable[Section] {
    def findBranding(edition: String)(s: Section) = findBrandingFromSection(s, edition, publishedDate = None)
  }

  implicit object BrandableTag extends Brandable[Tag] {
    def findBranding(edition: String)(t: Tag) = findBrandingFromTag(t, edition, publishedDate = None)
  }
}
