package com.smartnsoft.smartapprating

import android.content.Context
import com.smartnsoft.smartapprating.bo.Configuration

/**
 *
 * @author Cyllene
 * @since 2019.10.11
 */
class RemoteSmartAppRatingManager(
    applicationId: String,
    applicationVersionName: String,
    isInDevelopmentMode: Boolean,
    configuration: Configuration? = null,
    applicationContext: Context
) : SmartAppRatingManager(applicationId, applicationVersionName, isInDevelopmentMode, configuration, applicationContext)
{

  override fun fetchConfigurationAndTryToDisplayPopup()
  {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun fetchConfigurationDisplayPopupWithoutVerification()
  {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun fetchConfiguration()
  {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun fetchConfigurationSync(): Boolean
  {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

}