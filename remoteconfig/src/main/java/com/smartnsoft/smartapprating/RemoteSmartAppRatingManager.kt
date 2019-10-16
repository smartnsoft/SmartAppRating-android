package com.smartnsoft.smartapprating

import android.content.Context
import android.util.Log
import androidx.annotation.WorkerThread
import com.google.android.gms.tasks.Tasks
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.smartnsoft.smartapprating.bo.Configuration
import com.smartnsoft.smartapprating.bo.RemoteConfigMatchingInformation
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.io.File
import java.util.concurrent.TimeUnit

/**
 *
 * @author Adrien Vitti
 * @since 2019.10.11
 */
class RemoteSmartAppRatingManager(
    applicationId: String,
    applicationVersionName: String,
    isInDevelopmentMode: Boolean,
    configuration: Configuration? = null,
    applicationContext: Context,
    private val remoteConfigMatchingInformation: RemoteConfigMatchingInformation,
    remoteConfigCacheExpiration: Long,
    val synchronousTimeoutInMillisecond: Long
) : SmartAppRatingManager(applicationId, applicationVersionName, isInDevelopmentMode, configuration, applicationContext)
{

  companion object
  {

    const val MAXIMUM_CACHE_RETENTION_FOR_REMOTE_CONFIG_IN_SECONDS = (24 * 60 * 60).toLong()
    const val SYNCHRONISATION_TIMEOUT_IN_MILLISECONDS = (10 * 1000).toLong()

  }

  private val firebaseRemoteConfig: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()

  init
  {
    val remoteConfigSettings = FirebaseRemoteConfigSettings.Builder()
        .setFetchTimeoutInSeconds(synchronousTimeoutInMillisecond)
        .setMinimumFetchIntervalInSeconds(
            if (isInDevelopmentMode)
            {
              0
            }
            else
            {
              remoteConfigCacheExpiration
            }
        )
        .setDeveloperModeEnabled(isInDevelopmentMode)
        .build()
    log.logLevel = if (isInDevelopmentMode) Log.DEBUG else Log.WARN
    firebaseRemoteConfig.setConfigSettingsAsync(remoteConfigSettings).addOnCompleteListener {
      if (log.isDebugEnabled)
      {
        log.debug("Firebase RemoteConfig custom settings have been applied !")
      }
    }
  }

  override fun fetchConfigurationAndTryToDisplayPopup()
  {
    fetchRemoteConfig {
      onUpdateSuccessful(true)
    }
  }

  override fun fetchConfigurationDisplayPopupWithoutVerification()
  {
    fetchRemoteConfig {
      onUpdateSuccessful(showPopup = true, withoutVerification = true)
    }
  }

  override fun fetchConfiguration()
  {
    fetchRemoteConfig {
      onUpdateSuccessful(false)
    }
  }

  @WorkerThread
  override fun fetchConfigurationSync(): Boolean
  {
    val startTime = System.currentTimeMillis()
    var success = false

    try
    {
      success = Tasks.await(firebaseRemoteConfig.fetchAndActivate(), synchronousTimeoutInMillisecond, TimeUnit.MILLISECONDS)
      if (isInDevelopmentMode)
      {
        log.debug("Synchronous Remote Config retrieving task took ${(System.currentTimeMillis() - startTime)}ms")
      }
    }
    catch (exception: java.lang.Exception)
    {
      if (isInDevelopmentMode)
      {
        log.warn("Synchronous Remote Config retrieving task has failed", exception)
      }
    }
    finally
    {
      return success
    }
  }

  private fun onUpdateSuccessful(showPopup: Boolean, withoutVerification: Boolean = false)
  {
    log.warn("onUpdateSuccessful")
    storeConfiguration(createRemoteConfiguration())
    if (showPopup)
    {
      try
      {
        if (withoutVerification)
        {
          showRatePopupWithoutVerification()
        }
        else
        {
          showRatePopup()
        }
      }
      catch (exception: java.lang.Exception)
      {
        if (log.isWarnEnabled)
        {
          log.warn("Unable to display rating popup", exception)
        }
      }
    }
  }

  private fun createRemoteConfiguration(): Configuration
  {
    val jsonField = firebaseRemoteConfig.getString(remoteConfigMatchingInformation.jsonField)
    if (jsonField.isNullOrBlank().not())
    {
      val moshiParser = Moshi.Builder().build()
      try
      {
        moshiParser.adapter<Configuration>(Configuration::class.java).fromJson(jsonField)?.also { parsedConfiguration ->
          return parsedConfiguration
        }
      }
      catch (exception: Exception)
      {
        if (log.isWarnEnabled)
        {
          log.warn("Unable to parse json in Remote Config", exception)
        }
      }
    }

    return Configuration().apply {
      isRateAppDisabled = firebaseRemoteConfig.getBoolean(remoteConfigMatchingInformation.rateAppDisabled)
      numberOfSessionBeforeAskingToRate = firebaseRemoteConfig.getLong(remoteConfigMatchingInformation.displaySessionCount).toInt()
      maxNumberOfReminder = firebaseRemoteConfig.getLong(remoteConfigMatchingInformation.maxNumberOfReminders).toInt()
      minimumTimeGapAfterACrashInDays = firebaseRemoteConfig.getDouble(remoteConfigMatchingInformation.minimumTimeGapAfterACrashInDays)
      minimumTimeGapBeforeAskingAgainInDays = firebaseRemoteConfig.getDouble(remoteConfigMatchingInformation.minimumTimeGapBeforeAskingAgainInDays)
      maxDaysBetweenSession = firebaseRemoteConfig.getLong(remoteConfigMatchingInformation.maxDaysBetweenSession).toInt()

      ratePopupTitle = firebaseRemoteConfig.getString(remoteConfigMatchingInformation.ratePopupTitle)
      ratePopupContent = firebaseRemoteConfig.getString(remoteConfigMatchingInformation.ratePopupContent)
      minimumNumberOfStarBeforeRedirectToStore = firebaseRemoteConfig.getLong(remoteConfigMatchingInformation.minimumNumberOfStarBeforeRedirectToStore).toInt()

      likeActionButtonText = firebaseRemoteConfig.getString(remoteConfigMatchingInformation.likeActionButtonText)
      likeExitButtonText = firebaseRemoteConfig.getString(remoteConfigMatchingInformation.likeExitButtonText)
      likePopupContent = firebaseRemoteConfig.getString(remoteConfigMatchingInformation.likePopupContent)
      likePopupTitle = firebaseRemoteConfig.getString(remoteConfigMatchingInformation.likePopupTitle)

      supportEmailSubject = firebaseRemoteConfig.getString(remoteConfigMatchingInformation.supportEmailSubject)
      supportEmailHeader = firebaseRemoteConfig.getString(remoteConfigMatchingInformation.supportEmailHeader)
      supportEmail = firebaseRemoteConfig.getString(remoteConfigMatchingInformation.supportEmail)

      dislikeActionButtonText = firebaseRemoteConfig.getString(remoteConfigMatchingInformation.dislikeActionButtonText)
      dislikeExitButtonText = firebaseRemoteConfig.getString(remoteConfigMatchingInformation.dislikeExitButtonText)
      dislikePopupContent = firebaseRemoteConfig.getString(remoteConfigMatchingInformation.dislikePopupContent)
      dislikePopupTitle = firebaseRemoteConfig.getString(remoteConfigMatchingInformation.dislikePopupTitle)
    }
  }

  private fun fetchRemoteConfig(onUpdateSuccessful: () -> Unit)
  {
    firebaseRemoteConfig.fetchAndActivate().addOnCompleteListener { task ->
      if (task.isSuccessful)
      {
        runBlocking(Dispatchers.Default) {
          onUpdateSuccessful()
        }
      }
    }
  }

}

@Suppress("unused")
class RemoteConfigFactory
@JvmOverloads
constructor
(
    private val matchingInformation: RemoteConfigMatchingInformation = RemoteConfigMatchingInformation(),
    private val remoteConfigExpirationInSeconds: Long = RemoteSmartAppRatingManager.MAXIMUM_CACHE_RETENTION_FOR_REMOTE_CONFIG_IN_SECONDS,
    private val synchronousTimeoutInMillisecond: Long = RemoteSmartAppRatingManager.SYNCHRONISATION_TIMEOUT_IN_MILLISECONDS
) : SmartAppRatingManager.SmartAppRatingFactory
{

  override fun create(
      isInDevelopmentMode: Boolean,
      baseURL: String?,
      configurationFilePath: String?,
      configuration: Configuration?,
      context: Context,
      cacheDirectory: File?,
      cacheSize: Int,
      appId: String,
      appVersionName: String
  ): SmartAppRatingManager
  {
    return RemoteSmartAppRatingManager(appId, appVersionName, isInDevelopmentMode, configuration, context, matchingInformation, remoteConfigExpirationInSeconds, synchronousTimeoutInMillisecond)
  }


}