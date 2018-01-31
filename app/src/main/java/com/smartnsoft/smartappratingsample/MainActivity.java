package com.smartnsoft.smartappratingsample;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.smartnsoft.smartapprating.SmartAppRatingManager;
import com.smartnsoft.smartapprating.SmartAppRatingManager.ApplicationInformationProvider;
import com.smartnsoft.smartapprating.SmartAppRatingManager.Builder;

public class MainActivity
    extends AppCompatActivity
{

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

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
            return "com.smartnsoft.metro";
            //return BuildConfig.APPLICATION_ID;
          }

          @Override
          public long getLatestCrashDate()
          {
            return 0;
          }
        })
        .setConfigurationFileURL("http://smartdistrib.com/", "shared/lci/rateConfiguration.json")
        .build();

    smartAppRatingManager.fetchConfigurationAndTryToDisplayPopup();
  }

}
