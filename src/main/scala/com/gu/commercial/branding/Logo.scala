package com.gu.commercial.branding

import com.gu.contentapi.client.model.v1.{SponsorshipLogoDimensions, SponsorshipType}

case class Logo(
  src: String,
  dimensions: Option[Dimensions],
  link: String,
  label: String
)

object Logo {

  val sensitiveTitles = Map("Inequality" -> "The Inequality Project",
                            "America's unequal future" -> "This series")

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
        case SponsorshipType.Foundation =>
          s"${sensitiveTitles.getOrElse(title, title)} is supported by"
        case _ => "Supported by"
      }
    )
  }
}

case class Dimensions(width: Int, height: Int)
