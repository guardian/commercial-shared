package com.gu.commercial.branding

import com.gu.contentapi.client.model.v1.{SponsorshipPackage, SponsorshipType}
import org.scalatest.OptionValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class LogoSpec extends AnyFlatSpec with Matchers with OptionValues {

  private def mkLogo(sponsorshipType: SponsorshipType, sponsorshipPackage: Option[SponsorshipPackage], webTitle: String = "") = Logo.make(
    title = webTitle,
    sponsorshipType = sponsorshipType,
    sponsorshipPackage = sponsorshipPackage,
    src = "src",
    dimensions = None,
    link = "link"
  )

  "make" should "generate the correct label for sponsored content with unspecified sponsorship package" in {
    val logo = mkLogo(SponsorshipType.Sponsored, None)
    logo.label shouldBe "Supported by"
  }

  "make" should "generate the correct label for sponsored content with default sponsorship package" in {
    val logo = mkLogo(SponsorshipType.Sponsored, Some(SponsorshipPackage.Default))
    logo.label shouldBe "Supported by"
  }

  "make" should "generate the correct label for sponsored content with US sponsorship package" in {
    val logo = mkLogo(SponsorshipType.Sponsored, Some(SponsorshipPackage.Us))
    logo.label shouldBe "Advertising partner"
  }

  "make" should "generate the correct label for sponsored content with US exclusive package" in {
    val logo = mkLogo(SponsorshipType.Sponsored, Some(SponsorshipPackage.UsExclusive))
    logo.label shouldBe "Exclusive advertising partner"
  }

  it should "generate the correct label for paid content" in {
    val logo = mkLogo(SponsorshipType.PaidContent, None)
    logo.label shouldBe "Paid for by"
  }

  it should "generate the correct label for foundation-funded content" in {
    val logo = mkLogo(SponsorshipType.Foundation, None, webTitle = "Some title")
    logo.label shouldBe "Some title is supported by"
  }

  it should "generate the correct label for the special inequality foundation-funded section" in {
    val logo = mkLogo(SponsorshipType.Foundation, None, webTitle = "Inequality")
    logo.label shouldBe "The Inequality Project is supported by"
  }

  it should "generate the correct label for the special America's unequal future foundation-funded section" in {
    val logo = mkLogo(SponsorshipType.Foundation, None, webTitle = "Inequality and Opportunity in America")
    logo.label shouldBe "This series is supported by"
  }
}
