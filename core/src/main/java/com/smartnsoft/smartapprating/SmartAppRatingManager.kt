package com.smartnsoft.smartapprating

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.TextUtils
import android.util.Log
import androidx.annotation.AnyThread
import androidx.annotation.WorkerThread
import com.smartnsoft.logger.Logger
import com.smartnsoft.logger.LoggerFactory
import com.smartnsoft.smartapprating.bo.Configuration
import com.smartnsoft.smartapprating.utils.DateUtils
import java.io.IOException
import java.util.*

/**
 *
 * @author Adrien Vitti
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

  interface SmartAppRatingFactory
  {

    fun create(
        isInDevelopmentMode: Boolean,
        configuration: Configuration?,
        context: Context,
        appId: String,
        appVersionName: String
    ): SmartAppRatingManager
  }

  class Builder(private val context: Context)
  {

    private var isInDevelopmentMode: Boolean = false

    private var configuration: Configuration? = null

    private var ratePopupActivity: Class<out AbstractSmartAppRatingActivity>? = null

    private var applicationId: String? = null

    private var applicationVersionName: String? = null

    private var factory: SmartAppRatingFactory = LocalConfigFactory()

    fun setIsInDevelopmentMode(isInDevelopmentMode: Boolean): Builder
    {
      this.isInDevelopmentMode = isInDevelopmentMode
      return this
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

    fun setFactory(factory: SmartAppRatingFactory): Builder
    {
      this.factory = factory
      return this
    }

    fun build(): SmartAppRatingManager
    {
      val appId = applicationId ?: ""
      val appVersionName = applicationVersionName ?: ""

      check(!TextUtils.isEmpty(appId)) { "Unable to create the app rating manager because the application ID was not set" }
      check(!TextUtils.isEmpty(appVersionName)) { "Unable to create the app rating manager because the application ID was not set" }

      val smartAppRatingManager = factory.create(isInDevelopmentMode, configuration, context, appId, appVersionName)

      smartAppRatingManager.configuration = configuration
      smartAppRatingManager.log.logLevel = if (isInDevelopmentMode) Log.DEBUG else Log.WARN
      ratePopupActivity?.also { ratePopupActivity ->
        smartAppRatingManager.ratingPopupActivityClass = ratePopupActivity
      }
      return smartAppRatingManager
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
    private fun getLastSessionDate(preferences: SharedPreferences): Long
    {
      return preferences.getLong(LAST_SESSION_DATE_FOR_APP_RATING_PREFERENCE_KEY, 0)
    }

    @JvmStatic
    private fun setLastSessionDate(preferences: SharedPreferences, lastSessionDate: Long)
    {
      preferences.edit().putLong(LAST_SESSION_DATE_FOR_APP_RATING_PREFERENCE_KEY, lastSessionDate).apply()
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

    @JvmStatic
    fun backupSettings(preferences: SharedPreferences): Bundle
    {
      return Bundle().apply {
        putLong(LAST_RATE_POPUP_CLICK_ON_LATER_TIMESTAMP_PREFERENCE_KEY, getRateLaterTimestamp(preferences))
        putBoolean(RATING_HAS_BEEN_GIVEN_PREFERENCE_KEY, hasRatingAlreadyBeenGiven(preferences))
        putLong(NUMBER_OF_SESSION_PREFERENCE_KEY, getNumberOfSession(preferences))
        putLong(NUMBER_OF_TIME_LATER_WAS_CLICKED_PREFERENCE_KEY, getNumberOfTimeLaterWasClicked(preferences))
        putLong(LAST_SESSION_DATE_FOR_APP_RATING_PREFERENCE_KEY, getLastSessionDate(preferences))
        putLong(LAST_CRASH_TIMESTAMP_PREFERENCE_KEY, getLastCrashTimestamp(preferences))
      }
    }

    @JvmStatic
    fun restoreSettings(preferences: SharedPreferences, bundle: Bundle?)
    {
      bundle?.also { backup ->
        preferences.edit().apply {
          putLong(LAST_RATE_POPUP_CLICK_ON_LATER_TIMESTAMP_PREFERENCE_KEY,
              backup.getLong(LAST_RATE_POPUP_CLICK_ON_LATER_TIMESTAMP_PREFERENCE_KEY, getRateLaterTimestamp(preferences)))

          putBoolean(RATING_HAS_BEEN_GIVEN_PREFERENCE_KEY,
              backup.getBoolean(RATING_HAS_BEEN_GIVEN_PREFERENCE_KEY, hasRatingAlreadyBeenGiven(preferences)))

          putLong(NUMBER_OF_SESSION_PREFERENCE_KEY,
              backup.getLong(NUMBER_OF_SESSION_PREFERENCE_KEY, getNumberOfSession(preferences)))

          putLong(NUMBER_OF_TIME_LATER_WAS_CLICKED_PREFERENCE_KEY,
              backup.getLong(NUMBER_OF_TIME_LATER_WAS_CLICKED_PREFERENCE_KEY, getNumberOfTimeLaterWasClicked(preferences))
          )

          putLong(LAST_SESSION_DATE_FOR_APP_RATING_PREFERENCE_KEY,
              backup.getLong(LAST_SESSION_DATE_FOR_APP_RATING_PREFERENCE_KEY, getLastSessionDate(preferences))
          )

          putLong(LAST_CRASH_TIMESTAMP_PREFERENCE_KEY,
              backup.getLong(LAST_CRASH_TIMESTAMP_PREFERENCE_KEY, getLastCrashTimestamp(preferences))
          )
          apply()
        }
      }
    }

  }

  @JvmField
  protected val log: Logger = LoggerFactory.getInstance("SmartAppRatingManager")

  /**
   * This method will display the rating popup if conditions are met.
   * To force the popup, you must activate development mode and set [withoutVerification] to true
   */
  @JvmOverloads
  fun showRatePopup(withoutVerification: Boolean = false)
  {
    val ratingPopupIntent = getRatingPopupIntent(withoutVerification)
    if (ratingPopupIntent != null)
    {
      if (log.isDebugEnabled)
      {
        log.debug("Try to display the rating popup")
      }
      applicationContext.startActivity(ratingPopupIntent)
    }
  }

  fun increaseSessionNumberIfConditionsAreMet(sharedPreferences: SharedPreferences)
  {
    val lastSessionDateInMilliseconds = getLastSessionDate(sharedPreferences)
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
    setLastSessionDate(sharedPreferences, currentTimeMillis)
  }

  /**
   * This method will give you the intent to launch the rating popup intent if conditions are met.
   * To force the creation of the intent, you must activate development mode and set
   * [withoutVerification] to true
   */
  @JvmOverloads
  fun getRatingPopupIntent(withoutVerification: Boolean = false): Intent?
  {
    if (withoutVerification)
    {
      return configuration
          ?.takeIf { isInDevelopmentMode }
          ?.let {
            createRatePopupIntent()
          }
    }
    else
    {
      val sharedPreferences = getPreferences()
      return configuration
          ?.takeIf { conf ->
            conf.isRateAppDisabled.not()
                && hasRatingAlreadyBeenGiven(sharedPreferences).not()
                && conf.minimumTimeGapAfterACrashInDays > 0 && getLastCrashTimestamp(sharedPreferences) + conf.minimumTimeGapAfterACrashInDays * DAY_IN_MILLISECONDS < System.currentTimeMillis()
                && conf.minimumTimeGapBeforeAskingAgainInDays > 0 && getRateLaterTimestamp(sharedPreferences) + conf.minimumTimeGapBeforeAskingAgainInDays * DAY_IN_MILLISECONDS < System.currentTimeMillis()
                && conf.numberOfSessionBeforeAskingToRate > 0 && conf.numberOfSessionBeforeAskingToRate <= getNumberOfSession(sharedPreferences)
                && conf.maxNumberOfReminder > 0 && conf.maxNumberOfReminder > getNumberOfTimeLaterWasClicked(sharedPreferences)
          }
          ?.let { _ ->
            if (log.isDebugEnabled)
            {
              log.debug("Try to display the rating popup")
            }
            createRatePopupIntent()
          }
    }
  }

  /**
   * This method will retrieve configuration and then try to display the rating popup if conditions
   * are met and [tryToDisplayPopup] is set to true.
   * To force the popup, you must activate development mode and set [withoutVerification] to true
   */
  @AnyThread
  @JvmOverloads
  fun fetchConfig(tryToDisplayPopup: Boolean = false, withoutVerification: Boolean = false)
  {
    fetchConfiguration(tryToDisplayPopup, withoutVerification)
  }

  /**
   *
   * @return true if the configuration file has been retrieved, false otherwise
   * @throws IOException The exception thrown by the network call
   */
  @WorkerThread
  @Throws(IOException::class)
  abstract fun fetchConfigurationSync(): Boolean

  protected fun getPreferences(): SharedPreferences
  {
    return PreferenceManager.getDefaultSharedPreferences(applicationContext)
  }

  @AnyThread
  protected abstract fun fetchConfiguration(tryToDisplayPopup: Boolean = false, withoutVerification: Boolean = false)

  protected fun storeConfiguration(configuration: Configuration?)
  {
    if (log.isDebugEnabled)
    {
      log.debug("Configuration file has been retrieved with success !")
    }
    this.configuration = configuration
    increaseSessionNumberIfConditionsAreMet(getPreferences())
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

}