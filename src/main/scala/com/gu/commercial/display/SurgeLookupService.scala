package com.gu.commercial.display

/**
  * This is intended to be implemented by Ophan API lookups.
  */
trait SurgeLookupService {

  /**
    * This is intended to be implemented by caching results from the Ophan 'surging' service.
    */
  def pageViewsPerMinute(pageId: String): Option[Int]
}
