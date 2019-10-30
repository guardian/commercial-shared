package com.gu.commercial.branding

import com.gu.contentapi.client.model.v1.{CapiDateTime, Section, Sponsorship, Tag}

private[branding] object SponsorshipHelper {

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
      sponsorship <- findRelevantSponsorship(sponsorships.toSeq, edition, publishedDate)
    } yield sponsorship
  }

  def findSponsorshipFromTag(tag: Tag, edition: String, publishedDate: Option[CapiDateTime]): Option[Sponsorship] = {
    for {
      sponsorships <- tag.activeSponsorships
      sponsorship <- findRelevantSponsorship(sponsorships.toSeq, edition, publishedDate)
    } yield sponsorship
  }
}
