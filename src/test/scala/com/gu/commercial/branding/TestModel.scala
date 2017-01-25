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

  case class StubSection(
    id: String,
    webTitle: String,
    activeSponsorships: Option[Seq[TestSponsorship]]
  ) extends Section {
    def webUrl: String = ""
    def apiUrl: String = ""
    def editions: Seq[Edition] = Nil
  }

  case class StubFields(isInappropriateForSponsorshipText: Option[String]) extends ContentFields {
    def isInappropriateForSponsorship: Option[Boolean] = isInappropriateForSponsorshipText.map(_.toBoolean)
    def headline: Option[String] = None
    def standfirst: Option[String] = None
    def trailText: Option[String] = None
    def byline: Option[String] = None
    def main: Option[String] = None
    def body: Option[String] = None
    def newspaperPageNumber: Option[Int] = None
    def starRating: Option[Int] = None
    def contributorBio: Option[String] = None
    def membershipAccess: Option[MembershipTier] = None
    def wordcount: Option[Int] = None
    def commentCloseDate: Option[CapiDateTime] = None
    def commentable: Option[Boolean] = None
    def creationDate: Option[CapiDateTime] = None
    def displayHint: Option[String] = None
    def firstPublicationDate: Option[CapiDateTime] = None
    def hasStoryPackage: Option[Boolean] = None
    def internalComposerCode: Option[String] = None
    def internalOctopusCode: Option[String] = None
    def internalPageCode: Option[Int] = None
    def internalStoryPackageCode: Option[Int] = None
    def isPremoderated: Option[Boolean] = None
    def lastModified: Option[CapiDateTime] = None
    def liveBloggingNow: Option[Boolean] = None
    def newspaperEditionDate: Option[CapiDateTime] = None
    def productionOffice: Option[Office] = None
    def publication: Option[String] = None
    def scheduledPublicationDate: Option[CapiDateTime] = None
    def secureThumbnail: Option[String] = None
    def shortUrl: Option[String] = None
    def shouldHideAdverts: Option[Boolean] = None
    def showInRelatedContent: Option[Boolean] = None
    def thumbnail: Option[String] = None
    def legallySensitive: Option[Boolean] = None
    def allowUgc: Option[Boolean] = None
    def sensitive: Option[Boolean] = None
    def lang: Option[String] = None
    def internalRevision: Option[Int] = None
    def internalContentCode: Option[Int] = None
    def isLive: Option[Boolean] = None
    def internalShortId: Option[String] = None
    def shortSocialShareText: Option[String] = None
    def socialShareText: Option[String] = None
    def bodyText: Option[String] = None
    def charCount: Option[Int] = None
    def internalVideoCode: Option[String] = None
  }

  case class StubTag(
    id: String,
    webTitle: String,
    activeSponsorships: Option[Seq[TestSponsorship]]
  ) extends Tag {
    def `type`: TagType = TagType.Keyword
    def sectionId: Option[String] = None
    def sectionName: Option[String] = None
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

  case class StubItem(
    id: String,
    publicationDateText: Option[String],
    section: Option[StubSection],
    fields: Option[StubFields],
    tags: Seq[StubTag]
  ) extends Content {
    def `type`: ContentType = ContentType.Article
    def sectionId: Option[String] = None
    def sectionName: Option[String] = None
    def webPublicationDate: Option[CapiDateTime] = publicationDateText.map(dateTextToCapiDateTime)
    def webTitle: String = ""
    def webUrl: String = ""
    def apiUrl: String = ""
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
