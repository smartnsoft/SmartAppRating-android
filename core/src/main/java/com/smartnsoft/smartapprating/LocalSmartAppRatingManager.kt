package com.smartnsoft.smartapprating

import android.content.Context
import com.smartnsoft.smartapprating.bo.Configuration

/**
 *
 * @author Cyllene
 * @since 2019.10.11
 */
class LocalSmartAppRatingManager(
    applicationId: String,
    applicationVersionName: String,
    isInDevelopmentMode: Boolean,
    configuration: Configuration? = null,
    applicationContext: Context
) : SmartAppRatingManager(applicationId, applicationVersionName, isInDevelopmentMode, configuration, applicationContext)
{

  override fun fetchConfigurationAndTryToDisplayPopup()
  {

  }

  override fun fetchConfigurationDisplayPopupWithoutVerification()
  {

  }

  override fun fetchConfiguration()
  {

  }

  override fun fetchConfigurationSync(): Boolean
  {
    return false
  }

}