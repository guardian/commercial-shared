package com.gu.commercial.branding

import org.scalatest.{FlatSpec, Matchers, OptionValues}

class BrandingSpec extends FlatSpec with Matchers with OptionValues {

  private def mkBranding(brandingType: BrandingType) = Branding(
    brandingType = brandingType,
    sponsorName = "sponsor",
    logo = Logo(
      src = "src",
      dimensions = None,
      link = "link",
      label = "label"
    ),
    logoForDarkBackground = None,
    aboutThisLink = Branding.defaultAboutThisLink,
    hostedCampaignColour = None
  )

  "isPaid" should "be true for paid content" in {
    mkBranding(PaidContent) shouldBe 'paid
  }

  it should "be false for non-paid content" in {
    mkBranding(Sponsored) should not be 'paid
  }

  "isSponsored" should "be true for sponsored content" in {
    mkBranding(Sponsored) shouldBe 'sponsored
  }

  it should "be false for non-sponsored content" in {
    mkBranding(PaidContent) should not be 'sponsored
  }

  "isFoundationFunded" should "be true for foundation-funded content" in {
    mkBranding(Foundation) shouldBe 'foundationFunded
  }

  it should "be false for non-foundation-funded content" in {
    mkBranding(PaidContent) should not be 'foundationFunded
  }
}
