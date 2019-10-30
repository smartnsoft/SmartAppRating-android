package com.smartnsoft.smartapprating

import android.os.Bundle

/**
 *
 * @author Adrien Vitti
 * @since 2018.02.13
 */

class SmartAppRatingActivity : AbstractSmartAppRatingActivity()
{

  override fun getVersionCode(): Int
  {
    return 0
  }

  override fun getDateForAnalytics(): String?
  {
    return null
  }

  override fun sendAnalyticsEvent(eventName: String, bundle: Bundle)
  {

  }

}
