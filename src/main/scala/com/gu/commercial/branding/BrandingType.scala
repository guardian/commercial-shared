package com.gu.commercial.branding

import com.gu.contentapi.client.model.v1.SponsorshipType

sealed trait BrandingType {
  def name: String
}

case object Sponsored extends BrandingType {
  override val name: String = "sponsored"
}

case object Foundation extends BrandingType {
  override val name: String = "foundation"
}

case object PaidContent extends BrandingType {
  override val name: String = "paid-content"
}

object BrandingType {
  def fromSponsorshipType(sponsorshipType: SponsorshipType): BrandingType = {
    sponsorshipType match {
      case SponsorshipType.PaidContent => PaidContent
      case SponsorshipType.Foundation => Foundation
      case _ => Sponsored
    }
  }
}
