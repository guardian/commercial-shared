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
    lazy val tagSponsorship = findSponsorshipFromTags(item.tags, item.webPublicationDate, edition)
    lazy val sectionSponsorship = item.section.flatMap(findSponsorshipFromSection(edition, item.webPublicationDate))

    if (inappropriateForBranding) None
    else (tagSponsorship orElse sectionSponsorship) map Branding.fromSponsorship
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
  def findBranding(config: CollectionConfig, content: Set[_ <: Content], edition: String): Option[Branding] = {
    val configuredForBranding = config.metadata.exists(_.contains(Branded))
    if (configuredForBranding) {
      content.toList match {
        case head :: tail =>
          findBranding(head, edition) filter { branding =>
            tail forall (item => findBranding(item, edition).contains(branding))
          }
        case Nil => None
      }
    } else None
  }

  def findBranding(section: Section, edition: String): Option[Branding] =
    findSponsorshipFromSection(edition, publishedDate = None)(section) map Branding.fromSponsorship

  def findBranding(tag: Tag, edition: String): Option[Branding] = ???
}

object SponsorshipHelper {

  def isTargetingEdition(edition: String)(sponsorship: Sponsorship): Boolean = {
    sponsorship.targeting.isEmpty || sponsorship.targeting.exists { t =>
      t.validEditions.isEmpty || t.validEditions.exists(_.contains(edition.toUpperCase))
    }
  }

  def isTargetingDate(optDate: Option[CapiDateTime])(sponsorship: Sponsorship): Boolean = {
    val dateLaterThanThreshold = for {
      targeting <- sponsorship.targeting
      threshold <- targeting.publishedSince
      date <- optDate
    } yield date.dateTime >= threshold.dateTime
    dateLaterThanThreshold getOrElse true
  }

  def findSponsorshipFromSection(edition: String, publishedDate: Option[CapiDateTime])
    (section: Section): Option[Sponsorship] = {
    section.activeSponsorships.flatMap(_.find { s =>
      isTargetingEdition(edition)(s) && isTargetingDate(publishedDate)(s)
    })
  }

  def findSponsorshipFromTags(
    tags: Seq[Tag],
    publishedDate: Option[CapiDateTime],
    edition: String
  ): Option[Sponsorship] =
    tags.view.flatMap(findSponsorshipFromTag(edition, publishedDate)(_)).headOption

  def findSponsorshipFromTag(edition: String, publishedDate: Option[CapiDateTime])(tag: Tag): Option[Sponsorship] = {
    tag.activeSponsorships.flatMap(_.find { s =>
      isTargetingEdition(edition)(s) && isTargetingDate(publishedDate)(s)
    })
  }
}
