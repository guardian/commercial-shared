package com.gu.commercial.branding

import com.gu.contentapi.client.model.v1.{Sponsorship, SponsorshipLogoDimensions, SponsorshipType}

case class Branding(
  brandingType: BrandingType,
  sponsorName: String,
  logo: Logo,
  logoForDarkBackground: Option[Logo],
  aboutThisLink: Option[String]
)

object Branding {

  def fromSponsorship(webTitle: String, sponsorship: Sponsorship): Branding = {
    Branding(
      brandingType = BrandingType.fromSponsorshipType(sponsorship.sponsorshipType),
      sponsorName = sponsorship.sponsorName,
      logo = Logo.make(
        title = webTitle,
        sponsorshipType = sponsorship.sponsorshipType,
        src = sponsorship.sponsorLogo,
        dimensions = sponsorship.sponsorLogoDimensions,
        link = sponsorship.sponsorLink
      ),
      logoForDarkBackground = sponsorship.highContrastSponsorLogo.map { src =>
        Logo.make(
          title = webTitle,
          sponsorshipType = sponsorship.sponsorshipType,
          src,
          dimensions = sponsorship.highContrastSponsorLogoDimensions,
          link = sponsorship.sponsorLink
        )
      },
      aboutThisLink = sponsorship.aboutLink
    )
  }
}

case class Logo(
  src: String,
  dimensions: Option[Dimensions],
  link: String,
  label: String
)

object Logo {

  def make(
    title: String,
    sponsorshipType: SponsorshipType,
    src: String,
    dimensions: Option[SponsorshipLogoDimensions],
    link: String
  ): Logo = {
    Logo(
      src = src,
      dimensions = dimensions.map(d => Dimensions(d.width, d.height)),
      link,
      label = sponsorshipType match {
        case SponsorshipType.PaidContent => "Paid for by"
        case SponsorshipType.Foundation => s"$title is supported by"
        case _ => "Supported by"
      }
    )
  }
}

case class Dimensions(width: Int, height: Int)
