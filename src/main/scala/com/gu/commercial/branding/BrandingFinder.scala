package com.gu.commercial.branding

import com.gu.contentapi.client.model.v1._

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
    lazy val tagSponsorship = item.tags.flatMap(t => findTagSponsorship(edition, item.webPublicationDate)(t)).headOption
    lazy val sectionSponsorship = item.section.flatMap(findSectionSponsorship(edition, item.webPublicationDate))

    if (inappropriateForBranding) None
    else (tagSponsorship orElse sectionSponsorship) map Branding.fromSponsorship
  }

  /**
    * Finds branding of a set of content items.
    *
    * @param items   Content items with <code>section</code>, <code>isInappropriateForSponsorship</code> field
    *                and all <code>tags</code> populated
    * @param edition eg. <code>uk</code>
    * @return Branding, if it should be applied, else None
    */
  def findBranding(items: Set[_ <: Content], edition: String): Option[Branding] = {
    items.toList match {
      case head :: tail =>
        findBranding(head, edition) flatMap { branding =>
          if (tail forall (item => findBranding(item, edition).contains(branding))) {
            Some(branding)
          }
          else None
        }
      case Nil => None
    }
  }

  def findBranding(section: Section, edition: String): Option[Branding] = ???

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

  def findSectionSponsorship(edition: String, publishedDate: Option[CapiDateTime])
    (section: Section): Option[Sponsorship] = {
    section.activeSponsorships.flatMap(_.find { s =>
      isTargetingEdition(edition)(s) && isTargetingDate(publishedDate)(s)
    })
  }

  def findTagSponsorship(edition: String, publishedDate: Option[CapiDateTime])(tag: Tag): Option[Sponsorship] = {
    tag.activeSponsorships.flatMap(_.find { s =>
      isTargetingEdition(edition)(s) && isTargetingDate(publishedDate)(s)
    })
  }
}
