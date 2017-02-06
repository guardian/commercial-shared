package com.gu.commercial.branding

import com.gu.contentapi.client.model.v1._
import com.gu.facia.api.models.CollectionConfig
import com.gu.facia.client.models.Branded

object BrandingFinder {

  import SponsorshipHelper._

  /**
    * Finds branding of a single content item.
    *
    * @param item    Content item with <code>section</code>, <code>isInappropriateForSponsorship</code> field
    *                and all <code>tags</code> populated
    * @param edition eg. <code>uk</code>
    * @return Branding, if it should be applied, else None
    */
  def findBranding(item: Content, edition: String): Option[Branding] = {
    val inappropriateForBranding = item.fields.exists(_.isInappropriateForSponsorship.contains(true))

    lazy val tagBranding = {
      val branding = for {
        tag <- item.tags.view
        branding <- findBranding(tag, edition, item.webPublicationDate)
      } yield branding
      branding.headOption
    }

    lazy val sectionBranding = for {
      section <- item.section
      branding <- findBranding(section, edition, item.webPublicationDate)
    } yield branding

    if (inappropriateForBranding) None
    else tagBranding orElse sectionBranding
  }

  /**
    * Finds branding of a set of content items in a container.
    *
    * @param config  Configuration of container holding this content
    * @param content Content items with <code>section</code>, <code>isInappropriateForSponsorship</code> field
    *                and all <code>tags</code> populated
    * @param edition eg. <code>uk</code>
    * @return Branding, if it should be applied, else None
    */
  def findBranding(config: CollectionConfig, content: Set[_ <: Content], edition: String): Option[ContainerBranding] = {
    val brandings = for {
      item <- content
      branding <- findBranding(item, edition)
    } yield branding
    findBranding(
      isConfiguredForBranding = config.metadata.exists(_.contains(Branded)),
      brandings
    )
  }

  def findBranding(isConfiguredForBranding: Boolean, brandings: Set[Branding]): Option[ContainerBranding] = {
    if (isConfiguredForBranding && brandings.nonEmpty) {
      val allSameBranding = brandings.tail forall (_ == brandings.head)
      if (allSameBranding) {
        Some(brandings.head)
      } else {
        val allPaidContent = brandings forall (_.brandingType == PaidContent)
        if (allPaidContent) {
          Some(PaidMultiSponsorBranding)
        }
        else None
      }
    }
    else None
  }

  def findBranding(section: Section, edition: String): Option[Branding] =
    findBranding(section, edition, publishedDate = None)

  def findBranding(tag: Tag, edition: String): Option[Branding] =
    findBranding(tag, edition, publishedDate = None)

  private def findBranding(section: Section, edition: String, publishedDate: Option[CapiDateTime]): Option[Branding] =
    for (sponsorship <- findSponsorshipFromSection(section, edition, publishedDate))
      yield Branding.fromSponsorship(section.webTitle, campaignColour = None, sponsorship)

  private def findBranding(tag: Tag, edition: String, publishedDate: Option[CapiDateTime]): Option[Branding] =
    for (sponsorship <- findSponsorshipFromTag(tag, edition, publishedDate))
      yield Branding.fromSponsorship(tag.webTitle, campaignColour = tag.paidContentCampaignColour, sponsorship)
}

object SponsorshipHelper {

  def isTargetingEdition(sponsorship: Sponsorship, edition: String): Boolean = {
    sponsorship.targeting.isEmpty || sponsorship.targeting.exists { t =>
      t.validEditions.isEmpty || t.validEditions.exists(_.contains(edition.toUpperCase))
    }
  }

  def isTargetingDate(sponsorship: Sponsorship, publishedDate: Option[CapiDateTime]): Boolean = {
    val dateLaterThanThreshold = for {
      targeting <- sponsorship.targeting
      threshold <- targeting.publishedSince
      date <- publishedDate
    } yield date.dateTime >= threshold.dateTime
    dateLaterThanThreshold getOrElse true
  }

  def findRelevantSponsorship(
    sponsorships: Seq[Sponsorship],
    edition: String,
    publishedDate: Option[CapiDateTime]
  ): Option[Sponsorship] = {
    sponsorships.find(s => isTargetingEdition(s, edition) && isTargetingDate(s, publishedDate))
  }

  def findSponsorshipFromSection(
    section: Section,
    edition: String,
    publishedDate: Option[CapiDateTime]
  ): Option[Sponsorship] = {
    for {
      sponsorships <- section.activeSponsorships
      sponsorship <- findRelevantSponsorship(sponsorships, edition, publishedDate)
    } yield sponsorship
  }

  def findSponsorshipFromTag(tag: Tag, edition: String, publishedDate: Option[CapiDateTime]): Option[Sponsorship] = {
    for {
      sponsorships <- tag.activeSponsorships
      sponsorship <- findRelevantSponsorship(sponsorships, edition, publishedDate)
    } yield sponsorship
  }
}
