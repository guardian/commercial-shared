package com.gu.commercial.display

/**
  * This is intended to be implemented by an Ophan API lookup.
  */
trait SurgeLookupService {

  /**
    * This is intended to be implemented by the Ophan 'surging' service.
    */
  def pageViewsPerMinute(pageId: String): Option[Int]
}
