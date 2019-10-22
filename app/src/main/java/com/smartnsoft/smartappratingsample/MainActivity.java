package com.smartnsoft.smartappratingsample;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity
    extends AppCompatActivity
{

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    SampleApplication.getRatingManager().fetchConfig(true, true);
  }

}
