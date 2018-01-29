package com.smartnsoft.smartappratingsample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.smartnsoft.smartapprating.SmartAppRatingManager;
import com.smartnsoft.smartapprating.SmartAppRatingManager.Builder;

public class MainActivity
    extends AppCompatActivity
{

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    final SmartAppRatingManager smartAppRatingManager = new Builder(this, BuildConfig.DEBUG)
        .setConfigurationFileURL("http://smartdistrib.com/", "shared/lci/rateConfiguration.json")
        .build();

    smartAppRatingManager.fetchConfigurationAndTryToDisplayPopup();
  }

}
