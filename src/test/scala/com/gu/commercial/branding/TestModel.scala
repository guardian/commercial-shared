package com.gu.commercial.branding

import java.time.LocalDateTime
import java.time.ZoneOffset.UTC
import java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME

import com.gu.contentapi.client.model.v1._

object TestModel {

  private def dateTextToCapiDateTime(s: String): CapiDateTime = TestCapiDateTime(
    dateTime = LocalDateTime.parse(s, ISO_OFFSET_DATE_TIME).toInstant(UTC).toEpochMilli,
    iso8601 = s
  )

  case class TestCapiDateTime(dateTime: Long, iso8601: String) extends CapiDateTime

  case class TestSponsorshipTargeting(
    publishedSinceText: Option[String],
    validEditions: Option[Seq[String]]
  ) extends SponsorshipTargeting {
    def publishedSince: Option[CapiDateTime] = publishedSinceText.map(dateTextToCapiDateTime)
  }

  case class TestLogoDimensions(width: Int, height: Int) extends SponsorshipLogoDimensions

  case class TestSponsorship(
    sponsorshipTypeName: String,
    sponsorName: String,
    sponsorLogo: String,
    sponsorLink: String,
    targeting: Option[TestSponsorshipTargeting],
    aboutLink: Option[String],
    sponsorLogoDimensions: Option[TestLogoDimensions],
    highContrastSponsorLogo: Option[String],
    highContrastSponsorLogoDimensions: Option[TestLogoDimensions]
  ) extends Sponsorship {
    def sponsorshipType: SponsorshipType = sponsorshipTypeName match {
      case "sponsored" => SponsorshipType.Sponsored
      case "paid-content" => SponsorshipType.PaidContent
      case "foundation" => SponsorshipType.Foundation
    }
  }

  case class StubSection(id: String, activeSponsorships: Option[Seq[TestSponsorship]]) extends Section {
    def webTitle: String = ""
    def webUrl: String = ""
    def apiUrl: String = ""
    def editions: Seq[Edition] = Nil
  }

  case class StubTag(id: String, activeSponsorships: Option[Seq[TestSponsorship]]) extends Tag {
    def `type`: TagType = TagType.Keyword
    def sectionId: Option[String] = None
    def sectionName: Option[String] = None
    def webTitle: String = ""
    def webUrl: String = ""
    def apiUrl: String = ""
    def references: Seq[Reference] = Nil
    def description: Option[String] = None
    def bio: Option[String] = None
    def bylineImageUrl: Option[String] = None
    def bylineLargeImageUrl: Option[String] = None
    def podcast: Option[Podcast] = None
    def firstName: Option[String] = None
    def lastName: Option[String] = None
    def emailAddress: Option[String] = None
    def twitterHandle: Option[String] = None
    def paidContentType: Option[String] = None
    def paidContentCampaignColour: Option[String] = None
    def rcsId: Option[String] = None
    def r2ContributorId: Option[String] = None
  }

  case class StubItem(id: String, publicationDateText: Option[String], section: Option[StubSection], tags: Seq[StubTag])
    extends Content {
    def `type`: ContentType = ContentType.Article
    def sectionId: Option[String] = None
    def sectionName: Option[String] = None
    def webPublicationDate: Option[CapiDateTime] = publicationDateText.map(dateTextToCapiDateTime)
    def webTitle: String = ""
    def webUrl: String = ""
    def apiUrl: String = ""
    def fields: Option[ContentFields] = None
    def elements: Option[Seq[Element]] = None
    def references: Seq[Reference] = Nil
    def isExpired: Option[Boolean] = None
    def blocks: Option[Blocks] = None
    def rights: Option[Rights] = None
    def crossword: Option[Crossword] = None
    def atoms: Option[Atoms] = None
    def stats: Option[ContentStats] = None
    def debug: Option[Debug] = None
    def isGone: Option[Boolean] = None
    def isHosted: Boolean = false
  }
}
