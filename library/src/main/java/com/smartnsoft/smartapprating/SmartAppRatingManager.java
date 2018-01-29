package com.smartnsoft.smartapprating;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.smartnsoft.smartapprating.bo.Configuration;
import com.smartnsoft.smartapprating.ws.SmartAppRatingServices;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author Adrien Vitti
 * @since 2018.01.29
 */
@SuppressWarnings("unused")
public final class SmartAppRatingManager
{

  public static class Builder
  {

    private boolean isInDevelopmentMode;

    private String baseURL;

    private String configurationFilePath;

    private File cacheDirectory = null;

    @NonNull
    private Context context;

    @Nullable
    private Class<? extends SmartAppRatingActivity> ratePopupActivity;

    private int cacheSize;

    public Builder(@NonNull Context context, boolean isInDevelopmentMode)
    {
      this.isInDevelopmentMode = isInDevelopmentMode;
      this.context = context;
    }

    public Builder setConfigurationFileURL(@NonNull final String baseURL, @NonNull final String configurationFilePath)
    {
      this.baseURL = baseURL;
      this.configurationFilePath = configurationFilePath;
      return this;
    }

    public Builder setRatePopupActivity(@NonNull Class<? extends SmartAppRatingActivity> ratePopupActivity)
    {
      this.ratePopupActivity = ratePopupActivity;
      return this;
    }

    public Builder setCachePolicy(@NonNull File cacheDirectory, @IntRange(from = 1024 * 1024) int cacheSize)
    {
      this.cacheDirectory = cacheDirectory;
      this.cacheSize = cacheSize;
      return this;
    }

    public SmartAppRatingManager build()
    {
      final SmartAppRatingManager smartAppRatingManager = new SmartAppRatingManager(context, baseURL, configurationFilePath, cacheDirectory, cacheSize);
      smartAppRatingManager.isInDevelopmentMode = isInDevelopmentMode;
      if (ratePopupActivity != null)
      {
        smartAppRatingManager.setRatingPopupActivityClass(ratePopupActivity);
      }
      return smartAppRatingManager;
    }
  }

  public static void setRateLaterTimestamp(@NonNull SharedPreferences preferences, long updateLaterTimestamp)
  {
    preferences.edit().putLong(SmartAppRatingManager.LAST_RATE_POPUP_CLICK_ON_LATER_TIMESTAMP_PREFERENCE_KEY, updateLaterTimestamp).apply();
  }

  public static long getRateLaterTimestamp(@NonNull SharedPreferences preferences)
  {
    return preferences.getLong(SmartAppRatingManager.LAST_RATE_POPUP_CLICK_ON_LATER_TIMESTAMP_PREFERENCE_KEY, -1);
  }

  private static final String LAST_RATE_POPUP_CLICK_ON_LATER_TIMESTAMP_PREFERENCE_KEY = "lastUpdatePopupClickOnLaterTimestamp";

  private static final String TAG = "SmartAppRatingManager";

  private final Context applicationContext;

  private final String configurationFilePath;

  private boolean isInDevelopmentMode;

  private Configuration configuration;

  private final SmartAppRatingServices smartAppRatingServices;

  private Class<? extends SmartAppRatingActivity> ratingPopupActivityClass = SmartAppRatingActivity.class;

  SmartAppRatingManager(@NonNull Context context, @NonNull final String baseURL,
      @NonNull final String configurationFilePath, @Nullable File cacheDirectory,
      int cacheSize)
  {
    this.applicationContext = context.getApplicationContext();
    this.configurationFilePath = configurationFilePath;
    this.smartAppRatingServices = SmartAppRatingServices.get(baseURL, cacheDirectory, cacheSize);
  }

  void setRatingPopupActivityClass(Class<? extends SmartAppRatingActivity> ratingPopupActivityClass)
  {
    this.ratingPopupActivityClass = ratingPopupActivityClass;
  }

  public void fetchConfigurationAndTryToDisplayPopup()
  {
    if (isInDevelopmentMode)
    {
      Log.d(TAG, "fetching configuration...");
    }
    this.smartAppRatingServices.getConfiguration(configurationFilePath, new Callback<Configuration>()
    {

      @Override
      public void onResponse(Call<Configuration> call, Response<Configuration> response)
      {
        if (response.isSuccessful())
        {
          configuration = response.body();
          showRatePopup();
        }
        else
        {
          if (isInDevelopmentMode)
          {
            Log.w(TAG, "Failed to retrieve configuration file : HTTP error code = " + response.code());
          }
        }
      }

      @Override
      public void onFailure(Call<Configuration> call, Throwable t)
      {
        if (isInDevelopmentMode)
        {
          Log.w(TAG, "Failed to retrieve configuration file", t);
        }
      }
    });
  }

  public void fetchConfiguration()
  {
    if (isInDevelopmentMode)
    {
      Log.d(TAG, "fetching configuration...");
    }
    this.smartAppRatingServices.getConfiguration(configurationFilePath, new Callback<Configuration>()
    {

      @Override
      public void onResponse(Call<Configuration> call, Response<Configuration> response)
      {
        if (response.isSuccessful())
        {
          configuration = response.body();
        }
        else
        {
          if (isInDevelopmentMode)
          {
            Log.w(TAG, "Failed to retrieve configuration file : HTTP error code = " + response.code());
          }
        }
      }

      @Override
      public void onFailure(Call<Configuration> call, Throwable t)
      {
        if (isInDevelopmentMode)
        {
          Log.w(TAG, "Failed to retrieve configuration file", t);
        }
      }
    });
  }

  private void showRatePopup()
  {
    if (configuration != null && configuration.isRateAppDisabled == false)
    {
      if (isInDevelopmentMode)
      {
        Log.d(TAG, "Try to display the rating popup");
      }
      final Intent intent = new Intent(applicationContext, ratingPopupActivityClass);
      intent.putExtra(SmartAppRatingActivity.CONFIGURATION_EXTRA, configuration);
      applicationContext.startActivity(intent);
    }
  }

}
