package com.smartnsoft.smartappratingsample;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.smartnsoft.smartapprating.SmartAppRatingManager;
import com.smartnsoft.smartapprating.SmartAppRatingManager.ApplicationInformationProvider;
import com.smartnsoft.smartapprating.SmartAppRatingManager.Builder;

/**
 *
 * @author Adrien Vitti
 * @since 2018.01.31
 */

public final class SampleApplication
    extends Application
{

  public static final String LAST_CRASH_DATE_PREFERENCE_KEY = "LAST_CRASH_DATE";

  @Override
  public void onCreate()
  {
    super.onCreate();

    final Thread.UncaughtExceptionHandler defaultHandler = Thread.getDefaultUncaughtExceptionHandler();

    Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler()
    {
      @Override
      public void uncaughtException(Thread thread, Throwable throwable)
      {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(SampleApplication.this);
        sharedPreferences.edit().putLong(LAST_CRASH_DATE_PREFERENCE_KEY, System.currentTimeMillis()).apply();
        defaultHandler.uncaughtException(thread, throwable);
      }
    });

    final SmartAppRatingManager smartAppRatingManager = new Builder(this)
        .setIsInDevelopmentMode(BuildConfig.DEBUG)
        .setApplicationInformationForRatingProvider(new ApplicationInformationProvider()
        {
          @Override
          public String getVersionName()
          {
            return BuildConfig.VERSION_NAME;
          }

          @NonNull
          @Override
          public String getApplicationID()
          {
            return BuildConfig.DEBUG ? "com.smartnsoft.metro" : BuildConfig.APPLICATION_ID;
          }

          @Override
          public long getLatestCrashDate()
          {
            final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(SampleApplication.this);
            return sharedPreferences.getLong(LAST_CRASH_DATE_PREFERENCE_KEY, 0);
          }
        })
        .setConfigurationFileURL("http://smartdistrib.com/", "shared/lci/rateConfiguration.json")
        .build();

    smartAppRatingManager.fetchConfigurationAndTryToDisplayPopup();
  }
}
