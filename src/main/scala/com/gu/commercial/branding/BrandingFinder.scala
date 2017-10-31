package com.gu.commercial.branding

object BrandingFinder {

  /**
    * Finds branding of a single content item, tag or section.
    *
    * @param brandable Content item with <code>section</code>, <code>isInappropriateForSponsorship</code> field
    *                  and all <code>tags</code> populated, or Tag, or Section
    * @param edition   in lowercase String format eg. <code>uk</code>
    */
  def findBranding[T: Brandable](edition: String)(brandable: T): Option[Branding] =
    implicitly[Brandable[T]].findBranding(edition)(brandable)

}
