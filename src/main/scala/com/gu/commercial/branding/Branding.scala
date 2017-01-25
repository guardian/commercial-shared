package com.gu.commercial.branding

import com.gu.contentapi.client.model.v1.Sponsorship

case class Branding(
  brandingType: BrandingType,
  sponsorName: String,
  logo: Logo,
  logoForDarkBackground: Option[Logo],
  aboutThisLink: Option[String]
)

object Branding {
  def fromSponsorship(sponsorship: Sponsorship): Branding = Branding(
    brandingType = BrandingType.fromSponsorshipType(sponsorship.sponsorshipType),
    sponsorName = sponsorship.sponsorName,
    logo = Logo(
      src = sponsorship.sponsorLogo,
      dimensions = sponsorship.sponsorLogoDimensions.map(d => Dimensions(d.width, d.height)),
      link = sponsorship.sponsorLink
    ),
    logoForDarkBackground = sponsorship.highContrastSponsorLogo.map { src =>
      Logo(
        src = src,
        dimensions = sponsorship.highContrastSponsorLogoDimensions.map(d => Dimensions(d.width, d.height)),
        link = sponsorship.sponsorLink
      )
    },
    aboutThisLink = sponsorship.aboutLink
  )
}

case class Logo(
  src: String,
  dimensions: Option[Dimensions],
  link: String
)

case class Dimensions(width: Int, height: Int)
