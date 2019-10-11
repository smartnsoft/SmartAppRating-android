package com.smartnsoft.smartapprating

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.text.TextUtils
import android.util.Log
import androidx.annotation.AnyThread
import androidx.annotation.IntRange
import androidx.annotation.WorkerThread
import com.smartnsoft.logger.Logger
import com.smartnsoft.logger.LoggerFactory
import com.smartnsoft.smartapprating.bo.Configuration
import com.smartnsoft.smartapprating.utils.DateUtils
import java.io.File
import java.io.IOException
import java.util.*

/**
 *
 * @author Cyllene
 * @since 2019.10.10
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
abstract class SmartAppRatingManager
@JvmOverloads
protected constructor(
    @JvmField
    protected val applicationId: String,
    @JvmField
    protected val applicationVersionName: String,
    @JvmField
    protected val isInDevelopmentMode: Boolean = false,
    @JvmField
    protected var configuration: Configuration? = null,
    @JvmField
    protected val applicationContext: Context,
    @JvmField
    protected var ratingPopupActivityClass: Class<out AbstractSmartAppRatingActivity> = SmartAppRatingActivity::class.java
)
{

  enum class ConfigurationType
  {

    JSON,
    LocalConfig,
    RemoteConfig

  }

  class Builder(private val context: Context)
  {

    private var isInDevelopmentMode: Boolean = false

    private var baseURL: String? = null

    private var configurationFilePath: String? = null

    private var cacheDirectory: File? = null

    private var configuration: Configuration? = null

    private var ratePopupActivity: Class<out AbstractSmartAppRatingActivity>? = null

    private var cacheSize: Int = 0

    private var applicationId: String? = null

    private var applicationVersionName: String? = null

    private var configurationType: ConfigurationType = ConfigurationType.LocalConfig

    fun setIsInDevelopmentMode(isInDevelopmentMode: Boolean): Builder
    {
      this.isInDevelopmentMode = isInDevelopmentMode
      return this
    }

    @JvmOverloads
    fun configureWithJSON(baseURL: String,
                          configurationFilePath: String,
                          cacheDirectory: File? = null,
                          @IntRange(from = (1024 * 1024).toLong()) cacheSize: Int = 0
    ): Builder
    {
      this.configurationType = ConfigurationType.JSON
      this.baseURL = baseURL
      this.configurationFilePath = configurationFilePath
      this.cacheDirectory = cacheDirectory
      this.cacheSize = cacheSize
      return this
    }

    fun configureWithRemoteConfig()
    {
      this.configurationType = ConfigurationType.RemoteConfig
    }

    fun configureWithLocalConfig()
    {
      this.configurationType = ConfigurationType.LocalConfig
    }

    fun setRatePopupActivity(
        ratePopupActivity: Class<out AbstractSmartAppRatingActivity>): Builder
    {
      this.ratePopupActivity = ratePopupActivity
      return this
    }

    fun setApplicationId(applicationId: String): Builder
    {
      this.applicationId = applicationId
      return this
    }

    fun setFallbackConfiguration(configuration: Configuration): Builder
    {
      this.configuration = configuration
      return this
    }

    fun setApplicationVersionName(applicationVersionName: String): Builder
    {
      this.applicationVersionName = applicationVersionName
      return this
    }

    fun build(): SmartAppRatingManager
    {
      val appId = applicationId ?: ""
      val appVersionName = applicationVersionName ?: ""
      check(!TextUtils.isEmpty(appId)) { "Unable to create the app rating manager because the application ID was not set" }
      check(!TextUtils.isEmpty(appVersionName)) { "Unable to create the app rating manager because the application ID was not set" }

      val smartAppRatingManager = when (configurationType)
      {
        ConfigurationType.JSON         ->
        {
          buildJson(appId, appVersionName)
        }
        ConfigurationType.RemoteConfig ->
        {
          buildRemote(appId, appVersionName)
        }
        else                           ->
        {
          buildLocal(appId, appVersionName)
        }
      }

      smartAppRatingManager.configuration = configuration
      smartAppRatingManager.log.logLevel = if (isInDevelopmentMode) Log.DEBUG else Log.WARN
      ratePopupActivity?.also { ratePopupActivity ->
        smartAppRatingManager.ratingPopupActivityClass = ratePopupActivity
      }
      return smartAppRatingManager
    }

    private fun buildJson(appId: String, appVersionName: String): SmartAppRatingManager
    {
      val baseApiUrl = baseURL ?: ""
      val configurationFilePathUrl = configurationFilePath ?: ""

      check((TextUtils.isEmpty(baseApiUrl) && TextUtils.isEmpty(configurationFilePathUrl)).not()) { "Unable to create the app rating manager because no base URL or path url were given" }

      return JsonSmartAppRatingManager(
          applicationContext = context,
          applicationId = appId,
          applicationVersionName = appVersionName,
          isInDevelopmentMode = isInDevelopmentMode,
          configurationFilePath = configurationFilePathUrl,
          baseURL = baseApiUrl,
          cacheDirectory = cacheDirectory,
          cacheSize = cacheSize
      )
    }

    private fun buildLocal(appId: String, appVersionName: String): SmartAppRatingManager
    {
      check(configuration != null) { "Unable to create the app rating manager because no configuration was given" }

      return LocalSmartAppRatingManager(
          applicationContext = context,
          applicationId = appId,
          applicationVersionName = appVersionName,
          isInDevelopmentMode = isInDevelopmentMode
      )
    }

    private fun buildRemote(appId: String, appVersionName: String): SmartAppRatingManager
    {
      return RemoteSmartAppRatingManager(
          applicationContext = context,
          applicationId = appId,
          applicationVersionName = appVersionName,
          isInDevelopmentMode = isInDevelopmentMode
      )
    }
  }

  companion object
  {

    protected const val LAST_RATE_POPUP_CLICK_ON_LATER_TIMESTAMP_PREFERENCE_KEY = "SmartAppRating_lastRateAppPopupClickOnLaterTimestamp"

    protected const val LAST_CRASH_TIMESTAMP_PREFERENCE_KEY = "SmartAppRating_lastCrashTimestamp"

    protected const val RATING_HAS_BEEN_GIVEN_PREFERENCE_KEY = "SmartAppRating_ratingHasBeenGiven"

    protected const val NUMBER_OF_SESSION_PREFERENCE_KEY = "SmartAppRating_numberOfSession"

    protected const val NUMBER_OF_TIME_LATER_WAS_CLICKED_PREFERENCE_KEY = "SmartAppRating_numberOfTimeLaterWasClicked"

    protected const val LAST_SESSION_DATE_FOR_APP_RATING_PREFERENCE_KEY = "SmartAppRating_lastSessionTimestamp"

    protected const val DAY_IN_MILLISECONDS = (24 * 60 * 60 * 1000).toLong()

    @JvmStatic
    fun setRateLaterTimestamp(preferences: SharedPreferences,
                              updateLaterTimestamp: Long)
    {
      preferences.edit().putLong(LAST_RATE_POPUP_CLICK_ON_LATER_TIMESTAMP_PREFERENCE_KEY, updateLaterTimestamp).apply()
    }

    @JvmStatic
    fun hasRatingAlreadyBeenGiven(preferences: SharedPreferences): Boolean
    {
      return preferences.getBoolean(RATING_HAS_BEEN_GIVEN_PREFERENCE_KEY, false)
    }

    @JvmStatic
    fun setRatingHasBeenGiven(preferences: SharedPreferences)
    {
      preferences.edit().putBoolean(RATING_HAS_BEEN_GIVEN_PREFERENCE_KEY, true).apply()
    }

    @JvmStatic
    fun resetRating(preferences: SharedPreferences)
    {
      preferences.edit()
          .remove(RATING_HAS_BEEN_GIVEN_PREFERENCE_KEY)
          .remove(NUMBER_OF_TIME_LATER_WAS_CLICKED_PREFERENCE_KEY)
          .remove(NUMBER_OF_SESSION_PREFERENCE_KEY)
          .apply()
    }

    @JvmStatic
    fun getLastCrashTimestamp(preferences: SharedPreferences): Long
    {
      return preferences.getLong(LAST_CRASH_TIMESTAMP_PREFERENCE_KEY, -1)
    }

    @JvmStatic
    fun getRateLaterTimestamp(preferences: SharedPreferences): Long
    {
      return preferences.getLong(LAST_RATE_POPUP_CLICK_ON_LATER_TIMESTAMP_PREFERENCE_KEY, -1)
    }

    @JvmStatic
    fun increaseNumberOfSession(preferences: SharedPreferences)
    {
      setNumberOfSession(preferences, getNumberOfSession(preferences) + 1)
    }

    @JvmStatic
    fun increaseNumberOfTimeLaterWasClicked(preferences: SharedPreferences)
    {
      setNumberOfTimeLaterWasClicked(preferences, getNumberOfTimeLaterWasClicked(preferences) + 1)
    }

    @JvmStatic
    fun setNumberOfSession(preferences: SharedPreferences,
                           newNumberOfSession: Long)
    {
      preferences.edit().putLong(NUMBER_OF_SESSION_PREFERENCE_KEY, newNumberOfSession).apply()
    }

    @JvmStatic
    fun setNumberOfTimeLaterWasClicked(preferences: SharedPreferences,
                                       numberOfTimeLaterButtonWasClicked: Long)
    {
      preferences.edit().putLong(NUMBER_OF_TIME_LATER_WAS_CLICKED_PREFERENCE_KEY, numberOfTimeLaterButtonWasClicked).apply()
    }

    @JvmStatic
    fun getNumberOfTimeLaterWasClicked(preferences: SharedPreferences): Long
    {
      return preferences.getLong(NUMBER_OF_TIME_LATER_WAS_CLICKED_PREFERENCE_KEY, 0)
    }

    @JvmStatic
    fun getNumberOfSession(preferences: SharedPreferences): Long
    {
      return preferences.getLong(NUMBER_OF_SESSION_PREFERENCE_KEY, 0)
    }

    @JvmStatic
    fun setUncaughtExceptionHandler(context: Context, defaultHandler: Thread.UncaughtExceptionHandler)
    {
      Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        sharedPreferences.edit().putLong(LAST_CRASH_TIMESTAMP_PREFERENCE_KEY, System.currentTimeMillis()).apply()
        defaultHandler.uncaughtException(thread, throwable)
      }
    }
  }

  @JvmField
  protected val log: Logger = LoggerFactory.getInstance("SmartAppRatingManager")

  /**
   * This method allow you to display a rating popup even if conditions are not met.
   * As a safety it cannot be used when developmentMode is not activated.
   */
  fun showRatePopupWithoutVerification()
  {
    val ratePopupIntentWithoutVerification = getRatePopupIntentWithoutVerification()

    if (ratePopupIntentWithoutVerification != null)
    {
      if (log.isDebugEnabled)
      {
        log.debug("Try to display the rating popup")
      }
      applicationContext.startActivity(ratePopupIntentWithoutVerification)
    }
  }

  private fun createRatePopupIntent(): Intent?
  {
    return configuration?.let { configuration ->
      configuration.versionName = applicationVersionName
      configuration.applicationID = applicationId
      Intent(applicationContext, ratingPopupActivityClass).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
        putExtra(AbstractSmartAppRatingActivity.CONFIGURATION_EXTRA, configuration)
        putExtra(AbstractSmartAppRatingActivity.IS_IN_DEVELOPMENT_MODE_EXTRA, isInDevelopmentMode)
      }
    }
  }

  /*
   * This method allow you to get the rating popup intent even if conditions are not met.
   * As a safety it will return null if developmentMode is not activated or if configuration is not set.
   */
  fun getRatePopupIntentWithoutVerification(): Intent?
  {
    if (isInDevelopmentMode)
    {
      if (configuration != null)
      {
        return createRatePopupIntent()
      }
    }
    return null
  }

  fun increaseSessionNumberIfConditionsAreMet(sharedPreferences: SharedPreferences)
  {
    val lastSessionDateInMilliseconds = sharedPreferences.getLong(LAST_SESSION_DATE_FOR_APP_RATING_PREFERENCE_KEY, 0)
    val currentTimeMillis = System.currentTimeMillis()
    val lastSessionDate = Date(lastSessionDateInMilliseconds)

    if (DateUtils.addDays(lastSessionDate, configuration!!.maxDaysBetweenSession).time < currentTimeMillis)
    {
      setNumberOfSession(sharedPreferences, 1)
    }
    else if (DateUtils.isSameDay(lastSessionDate, Date(currentTimeMillis)).not())
    {
      increaseNumberOfSession(sharedPreferences)
    }
    sharedPreferences.edit().putLong(LAST_SESSION_DATE_FOR_APP_RATING_PREFERENCE_KEY, currentTimeMillis).apply()
  }

  protected fun getPreferences(): SharedPreferences
  {
    return PreferenceManager.getDefaultSharedPreferences(applicationContext)
  }

  fun getRatingPopupIntent(): Intent?
  {
    val sharedPreferences = getPreferences()
    configuration?.let { conf ->
      if (conf.isRateAppDisabled.not()
          && hasRatingAlreadyBeenGiven(sharedPreferences).not()
          && conf.minimumTimeGapAfterACrashInDays > 0 && getLastCrashTimestamp(sharedPreferences) + conf.minimumTimeGapAfterACrashInDays * DAY_IN_MILLISECONDS < System.currentTimeMillis()
          && conf.minimumTimeGapBeforeAskingAgainInDays > 0 && getRateLaterTimestamp(sharedPreferences) + conf.minimumTimeGapBeforeAskingAgainInDays * DAY_IN_MILLISECONDS < System.currentTimeMillis()
          && conf.numberOfSessionBeforeAskingToRate > 0 && conf.numberOfSessionBeforeAskingToRate <= getNumberOfSession(sharedPreferences)
          && conf.maxNumberOfReminder > 0 && conf.maxNumberOfReminder > getNumberOfTimeLaterWasClicked(sharedPreferences))
      {
        if (log.isDebugEnabled)
        {
          log.debug("Try to display the rating popup")
        }
        return createRatePopupIntent()
      }
    }
    return null
  }

  fun showRatePopup()
  {
    val ratingPopupIntent = getRatingPopupIntent()
    if (ratingPopupIntent != null)
    {
      applicationContext.startActivity(ratingPopupIntent)
    }
  }


  @AnyThread
  abstract fun fetchConfigurationAndTryToDisplayPopup()

  /**
   * This method allow you to fetch and display a rating popup even if conditions are not met.
   * As a safety it cannot be used when developmentMode is not activated.
   */
  @AnyThread
  abstract fun fetchConfigurationDisplayPopupWithoutVerification()

  @AnyThread
  abstract fun fetchConfiguration()

  protected fun storeConfiguration(configuration: Configuration?)
  {
    if (log.isDebugEnabled)
    {
      log.debug("Configuration file has been retrieved with success !")
    }
    this.configuration = configuration
    increaseSessionNumberIfConditionsAreMet(getPreferences())
  }

  /**
   * @return true if the configuration file has been retrieved, false otherwise
   * @throws IOException The exception thrown by the network call
   */
  @WorkerThread
  @Throws(IOException::class)
  abstract fun fetchConfigurationSync(): Boolean

}