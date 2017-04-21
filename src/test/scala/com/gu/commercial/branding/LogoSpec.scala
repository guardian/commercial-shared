package com.gu.commercial.branding

import com.gu.contentapi.client.model.v1.SponsorshipType
import org.scalatest.{FlatSpec, Matchers, OptionValues}

class LogoSpec extends FlatSpec with Matchers with OptionValues {

  private def mkLogo(sponsorshipType: SponsorshipType, webTitle: String = "") = Logo.make(
    title = webTitle,
    sponsorshipType = sponsorshipType,
    src = "src",
    dimensions = None,
    link = "link"
  )

  "make" should "generate the correct label for sponsored content" in {
    val logo = mkLogo(SponsorshipType.Sponsored)
    logo.label shouldBe "Supported by"
  }

  it should "generate the correct label for paid content" in {
    val logo = mkLogo(SponsorshipType.PaidContent)
    logo.label shouldBe "Paid for by"
  }

  it should "generate the correct label for foundation-funded content" in {
    val logo = mkLogo(SponsorshipType.Foundation, webTitle = "Some title")
    logo.label shouldBe "Some title is supported by"
  }

  it should "generate the correct label for the special inequality foundation-funded section" in {
    val logo = mkLogo(SponsorshipType.Foundation, webTitle = "Inequality")
    logo.label shouldBe "The Inequality Project is supported by"
  }
}
