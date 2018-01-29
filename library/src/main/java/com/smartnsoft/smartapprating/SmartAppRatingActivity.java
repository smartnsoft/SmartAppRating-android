package com.smartnsoft.smartapprating;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;

import com.smartnsoft.smartapprating.bo.Configuration;

/**
 * The class description here.
 *
 * @author Adrien Vitti
 * @since 2018.01.29
 */

public class SmartAppRatingActivity
    extends AppCompatActivity
    implements OnClickListener
{

  private static String TAG = "SmartAppRatingActivity";

  public static final String CONFIGURATION_EXTRA = "configurationExtra";

  public static final String IS_IN_DEVELOPMENT_MODE_EXTRA = "isInDevelopmentModeExtra";

  protected TextView title;

  protected TextView paragraph;

  protected TextView action;

  protected RatingBar rateBar;

  protected TextView later;

  protected ImageView close;

  protected ImageView image;

  protected Configuration configuration;

  private boolean isInDevelopmentMode;

  @Override
  protected final void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(getLayoutId());
    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

    title = findViewById(R.id.title);
    paragraph = findViewById(R.id.paragraph);

    rateBar = findViewById(R.id.rateBar);
    rateBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener()
    {
      @Override
      public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser)
      {
        if (isInDevelopmentMode)
        {
          Log.d(TAG, "Rating is now = " + rating);
        }
      }
    });
    //    action = findViewById(R.id.action_button);
    //    if (action != null)
    //    {
    //      action.setOnClickListener(this);
    //    }

    image = findViewById(R.id.image);

    later = findViewById(R.id.later);
    if (later != null)
    {
      later.setOnClickListener(this);
    }

    close = findViewById(R.id.close);
    if (close != null)
    {
      close.setOnClickListener(this);
    }

    final Bundle bundle = getIntent().getExtras();
    if (bundle != null)
    {
      configuration = (Configuration) bundle.getSerializable(SmartAppRatingActivity.CONFIGURATION_EXTRA);
      isInDevelopmentMode = bundle.getBoolean(SmartAppRatingActivity.IS_IN_DEVELOPMENT_MODE_EXTRA, false);
      if (configuration == null)
      {
        finish();
      }
    }

    updateLayoutWithUpdateInformation(configuration);
  }

  @LayoutRes
  protected int getLayoutId()
  {
    return R.layout.rating_popup_activity;
  }

  protected void updateLayoutWithUpdateInformation(Configuration configuration)
  {
    setTitle(configuration.ratePopupTitle);
    setContent(configuration.ratePopupContent);
  }

  @Override
  public void onClick(View view)
  {
    if (view == action)
    {
      onActionButtonClick(configuration);
    }
    else if (view == later)
    {
      askLater();
    }
    else if (view == close)
    {
      dismiss();
    }
  }

  protected void onActionButtonClick(Configuration configuration)
  {

  }

  @Override
  protected void onPause()
  {
    super.onPause();
    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
  }

  protected void dismiss()
  {
    finish();
  }

  protected void askLater()
  {
    final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    SmartAppRatingManager.setRateLaterTimestamp(sharedPreferences, System.currentTimeMillis());
    finish();
  }

  protected void setTitle(@Nullable final String titleFromRemoteConfig)
  {
    if (TextUtils.isEmpty(titleFromRemoteConfig) == false)
    {
      title.setText(titleFromRemoteConfig);
    }
  }

  protected void setContent(@Nullable final String contentFromRemoteConfig)
  {
    if (TextUtils.isEmpty(contentFromRemoteConfig) == false)
    {
      paragraph.setText(contentFromRemoteConfig);
    }
  }

  protected void setButtonLabel(@Nullable final String buttonLabelFromRemoteConfig)
  {
    if (TextUtils.isEmpty(buttonLabelFromRemoteConfig) == false)
    {
      action.setText(buttonLabelFromRemoteConfig);
    }
  }

}
