package com.gu.commercial.branding

import com.gu.contentapi.client.model.v1.Sponsorship

case class Branding(
  brandingType: BrandingType,
  sponsorName: String,
  logo: Logo,
  logoForDarkBackground: Option[Logo]
)

object Branding {
  def fromSponsorship(sponsorship: Sponsorship): Branding = Branding(
    brandingType = BrandingType.fromSponsorshipType(sponsorship.sponsorshipType),
    sponsorName = sponsorship.sponsorName,
    logo = Logo(
      src = sponsorship.sponsorLogo,
      width = sponsorship.sponsorLogoDimensions.map(_.width),
      height = sponsorship.sponsorLogoDimensions.map(_.height),
      link = sponsorship.sponsorLink
    ),
    logoForDarkBackground = sponsorship.highContrastSponsorLogo.map { src =>
      Logo(
        src = src,
        width = sponsorship.highContrastSponsorLogoDimensions.map(_.width),
        height = sponsorship.highContrastSponsorLogoDimensions.map(_.height),
        link = sponsorship.sponsorLink
      )
    }
  )
}

case class Logo(
  src: String,
  width: Option[Int],
  height: Option[Int],
  link: String
)
