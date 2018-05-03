package com.smartnsoft.smartappratingsample;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.smartnsoft.smartapprating.SmartAppRatingManager;
import com.smartnsoft.smartapprating.SmartAppRatingManager.Builder;

/**
 * @author Adrien Vitti
 * @since 2018.01.31
 */

public final class SampleApplication
    extends Application
{


  @Override
  public void onCreate()
  {
    super.onCreate();

    final SmartAppRatingManager smartAppRatingManager = new Builder(this)
        .setIsInDevelopmentMode(BuildConfig.DEBUG)
        .setApplicationId(BuildConfig.DEBUG ? "com.smartnsoft.metro" : BuildConfig.APPLICATION_ID)
        .setRatePopupActivity(AnimatedSmartAppRatingActivity.class)
        .setApplicationVersionName(BuildConfig.VERSION_NAME)
        .setConfigurationFileURL("http://smartdistrib.com/", "shared/lci/rateConfiguration.json")
        .build();

    SmartAppRatingManager.setUncaughtExceptionHandler(this, Thread.getDefaultUncaughtExceptionHandler());
    final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    SmartAppRatingManager.increaseNumberOfSession(sharedPreferences);

    //    smartAppRatingManager.fetchConfigurationAndTryToDisplayPopup();
    //    smartAppRatingManager.fetchConfiguration();
    //    smartAppRatingManager.showRatePopup();
    smartAppRatingManager.fetchConfigurationDisplayPopupWithoutVerification();
  }
}
