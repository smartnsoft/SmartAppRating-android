package com.smartnsoft.smartapprating

import android.content.Context
import androidx.annotation.WorkerThread
import com.smartnsoft.smartapprating.bo.Configuration

/**
 *
 * @author Adrien Vitti
 * @since 2019.10.11
 */
class LocalSmartAppRatingManager(
    applicationId: String,
    applicationVersionName: String,
    isInDevelopmentMode: Boolean,
    configuration: Configuration? = null,
    applicationContext: Context
) : SmartAppRatingManager(
    applicationId,
    applicationVersionName,
    isInDevelopmentMode,
    configuration,
    applicationContext
)
{

  override fun fetchConfiguration(tryToDisplayPopup: Boolean, withoutVerification: Boolean)
  {

  }

  @WorkerThread
  override fun fetchConfigurationSync(): Boolean
  {
    return false
  }

}

@Suppress("unused")
class LocalConfigFactory : SmartAppRatingManager.SmartAppRatingFactory
{

  override fun create(
      isInDevelopmentMode: Boolean,
      configuration: Configuration?,
      context: Context,
      appId: String,
      appVersionName: String
  ): SmartAppRatingManager
  {
    check(configuration != null) { "Unable to create the app rating manager because no configuration was given" }
    return LocalSmartAppRatingManager(appId, appVersionName, isInDevelopmentMode, configuration, context)
  }

}