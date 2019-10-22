package com.smartnsoft.smartapprating

import android.content.Context
import android.text.TextUtils
import androidx.annotation.AnyThread
import androidx.annotation.IntRange
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.annotation.WorkerThread
import com.smartnsoft.smartapprating.bo.Configuration
import com.smartnsoft.smartapprating.ws.SmartAppRatingServices
import org.jetbrains.annotations.NotNull
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException

/**
 *
 * @author Adrien Vitti
 * @since 2019.10.11
 */
class JsonSmartAppRatingManager(@NotNull applicationContext: Context,
                                @NotNull applicationId: String,
                                @NotNull applicationVersionName: String,
                                isInDevelopmentMode: Boolean = false,
                                @org.jetbrains.annotations.Nullable configuration: Configuration? = null,
                                @NonNull val baseURL: String,
                                @NonNull configurationFilePath: String,
                                @Nullable val cacheDirectory: File? = null,
                                cacheSize: Int = 0)
  : SmartAppRatingManager(
    applicationId,
    applicationVersionName,
    isInDevelopmentMode,
    configuration,
    applicationContext
)
{

  private val smartAppRatingServices: SmartAppRatingServices?

  private var configurationFilePath: String? = null

  init
  {
    if (TextUtils.isEmpty(baseURL).not())
    {
      this.configurationFilePath = configurationFilePath
      this.smartAppRatingServices = SmartAppRatingServices.get(baseURL, cacheDirectory, cacheSize)
    }
    else
    {
      this.configurationFilePath = null
      this.smartAppRatingServices = null
    }
  }

  override fun fetchConfigurationAndTryToDisplayPopup(withoutVerification: Boolean)
  {
    this.smartAppRatingServices?.also { services ->
      if (isInDevelopmentMode)
      {
        log.debug("fetching configuration...")
      }
      services.getConfiguration(configurationFilePath, object : Callback<Configuration>
      {

        override fun onResponse(call: Call<Configuration>,
                                response: Response<Configuration>)
        {
          if (response.isSuccessful)
          {
            storeConfiguration(response.body())
            showRatePopup(withoutVerification)
          }
          else
          {
            if (log.isWarnEnabled)
            {
              log.warn("Failed to retrieve configuration file : HTTP error code = " + response.code())
            }
          }
        }

        override fun onFailure(call: Call<Configuration>, t: Throwable)
        {
          if (log.isWarnEnabled)
          {
            log.warn("Failed to retrieve configuration file", t)
          }
        }
      })
    } ?: run {
      if (isInDevelopmentMode)
      {
        log.debug("The SmartAppManager has been created without config URL, so we won't do anything.")
      }
    }
  }

  @AnyThread
  override fun fetchConfiguration()
  {
    this.smartAppRatingServices?.also { services ->
      if (isInDevelopmentMode)
      {
        log.debug("fetching configuration...")
      }
      services.getConfiguration(configurationFilePath, object : Callback<Configuration>
      {

        override fun onResponse(call: Call<Configuration>, response: Response<Configuration>)
        {
          if (response.isSuccessful)
          {
            storeConfiguration(response.body())
          }
          else
          {
            if (log.isWarnEnabled)
            {
              log.warn("Failed to retrieve configuration file : HTTP error code = " + response.code())
            }
          }
        }

        override fun onFailure(call: Call<Configuration>, t: Throwable)
        {
          if (log.isWarnEnabled)
          {
            log.warn("Failed to retrieve configuration file", t)
          }
        }
      })
    } ?: run {
      if (isInDevelopmentMode)
      {
        log.debug("The SmartAppManager has been created without config URL, so we won't do anything.")
      }
    }
  }

  /**
   * @return true if the configuration file has been retrieved, false otherwise
   * @throws IOException The exception thrown by the network call
   */
  @Throws(IOException::class)
  @WorkerThread
  override fun fetchConfigurationSync(): Boolean
  {
    this.smartAppRatingServices?.let { services ->
      val configuration = services.getConfiguration(configurationFilePath)
      val configurationHasBeenRetrieved = configuration != null
      if (configurationHasBeenRetrieved)
      {
        storeConfiguration(configuration)
      }
      return configurationHasBeenRetrieved
    } ?: run {
      if (isInDevelopmentMode)
      {
        log.debug("The SmartAppManager has been created without config URL, so we won't do anything.")
      }
      return false
    }
  }

}

@Suppress("unused")
class JsonConfigFactory
@JvmOverloads
constructor
(
    private val baseURL: String,
    private val configurationFilePath: String,
    private val cacheDirectory: File? = null,
    @IntRange(from = (1024 * 1024).toLong()) val cacheSize: Int = 0
) : SmartAppRatingManager.SmartAppRatingFactory
{

  override fun create(
      isInDevelopmentMode: Boolean,
      configuration: Configuration?,
      context: Context,
      appId: String,
      appVersionName: String
  ): SmartAppRatingManager
  {
    val baseApiUrl = baseURL
    val configurationFilePathUrl = configurationFilePath

    check((TextUtils.isEmpty(baseApiUrl) && TextUtils.isEmpty(configurationFilePathUrl)).not()) {
      "Unable to create the app rating manager because no base URL or path url were given"
    }

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

}