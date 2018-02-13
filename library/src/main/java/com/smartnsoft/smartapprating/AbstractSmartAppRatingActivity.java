package com.smartnsoft.smartapprating;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.OvershootInterpolator;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;

import com.smartnsoft.smartapprating.bo.Configuration;

/**
 * @author Adrien Vitti
 * @since 2018.01.29
 */
@SuppressWarnings({ "unused", "WeakerAccess" })
public abstract class AbstractSmartAppRatingActivity
    extends AppCompatActivity
    implements OnClickListener, OnRatingBarChangeListener, RatingScreenAnalyticsInterface
{

  private final static String TAG = "SmartAppRatingActivity";

  public static final String CONFIGURATION_EXTRA = "configurationExtra";

  public static final String IS_IN_DEVELOPMENT_MODE_EXTRA = "isInDevelopmentModeExtra";

  protected View firstScreen;

  protected View secondScreen;

  protected TextView title;

  protected TextView paragraph;

  protected RatingBar rateBar;

  protected TextView later;

  protected TextView dislikeExitButton;

  protected TextView dislikeActionButton;

  protected TextView dislikeTitle;

  protected TextView dislikeParagraph;

  protected Configuration configuration;

  private boolean isInDevelopmentMode;

  @Override
  protected final void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(getLayoutId());
    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

    sendRatingScreenDisplay();

    bindViews();

    final Bundle bundle = getIntent().getExtras();
    if (bundle != null)
    {
      configuration = (Configuration) bundle.getSerializable(AbstractSmartAppRatingActivity.CONFIGURATION_EXTRA);
      isInDevelopmentMode = bundle.getBoolean(AbstractSmartAppRatingActivity.IS_IN_DEVELOPMENT_MODE_EXTRA, false);
      if (configuration == null)
      {
        finish();
      }
    }

    setFirstScreenContent(configuration);
  }

  private void bindViews()
  {
    firstScreen = findViewById(R.id.rateMainView);
    secondScreen = findViewById(R.id.rateDislikeView);

    title = findViewById(R.id.title);
    paragraph = findViewById(R.id.paragraph);

    rateBar = findViewById(R.id.rateBar);
    rateBar.setOnRatingBarChangeListener(this);
    later = findViewById(R.id.later);
    if (later != null)
    {
      later.setOnClickListener(this);
    }

    dislikeTitle = findViewById(R.id.dislikeTitle);
    dislikeParagraph = findViewById(R.id.dislikeParagraph);

    dislikeActionButton = findViewById(R.id.dislikeActionButton);
    if (dislikeActionButton != null)
    {
      dislikeActionButton.setOnClickListener(this);
    }
    dislikeExitButton = findViewById(R.id.dislikeExitButton);
    if (dislikeExitButton != null)
    {
      dislikeExitButton.setOnClickListener(this);
    }

  }

  @Override
  public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser)
  {
    if (fromUser)
    {
      if (isInDevelopmentMode)
      {
        Log.d(TAG, "Rating is now = " + rating);
      }

      sendUserSetRating((int) rating);

      setSecondViewContent(configuration, rating >= configuration.minimumNumberOfStarBeforeRedirectToStore);

      firstScreen.animate().scaleX(0).scaleY(0).setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime)).setListener(new AnimatorListener()
      {
        @Override
        public void onAnimationStart(Animator animation)
        {

        }

        @Override
        public void onAnimationEnd(Animator animation)
        {
          firstScreen.setVisibility(View.GONE);
          secondScreen.setVisibility(View.VISIBLE);
          secondScreen.animate().scaleX(1).scaleY(1).setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime)).setInterpolator(new OvershootInterpolator()).start();
        }

        @Override
        public void onAnimationCancel(Animator animation)
        {

        }

        @Override
        public void onAnimationRepeat(Animator animation)
        {

        }
      }).start();
    }
  }

  @LayoutRes
  protected int getLayoutId()
  {
    return R.layout.smartapprating_popup_activity;
  }

  protected void setSecondViewContent(Configuration configuration, boolean isPositiveRating)
  {
    if (isPositiveRating)
    {
      setSecondScreenTitle(configuration.likePopupTitle);
      setSecondScreenParagraph(configuration.likePopupContent);
      setSecondScreenActionButtonText(configuration.likeActionButtonText);
      setSecondScreenLaterButtonText(configuration.likeExitButtonText);
    }
    else
    {
      setSecondScreenTitle(configuration.dislikePopupTitle);
      setSecondScreenParagraph(configuration.dislikePopupContent);
      setSecondScreenActionButtonText(configuration.dislikeActionButtonText);
      setSecondScreenLaterButtonText(configuration.dislikeExitButtonText);
    }
  }

  protected void setFirstScreenContent(Configuration configuration)
  {
    setFirstScreenTitle(configuration.ratePopupTitle);
    setFirstScreenParagraph(configuration.ratePopupContent);
    setFirstScreenLaterButtonText(configuration.likeExitButtonText);
  }

  @Override
  public void onBackPressed()
  {
    askLater();
    super.onBackPressed();
  }

  @Override
  public void onClick(View view)
  {
    if (view == dislikeActionButton)
    {
      if (rateBar.getRating() >= configuration.minimumNumberOfStarBeforeRedirectToStore)
      {
        // open store
        try
        {
          startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + configuration.applicationID)));
        }
        catch (android.content.ActivityNotFoundException anfe)
        {
          startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + configuration.applicationID)));
        }
      }
      else
      {
        startActivity(createSupportEmailIntent());
      }
      SmartAppRatingManager.setRatingHasBeenGiven(PreferenceManager.getDefaultSharedPreferences(this));
      finish();
    }
    else if (view == later || view == dislikeExitButton)
    {
      askLater();
    }
  }

  @NonNull
  protected Intent createSupportEmailIntent()
  {
    final Intent supportEmailIntent = new Intent(Intent.ACTION_SENDTO);
    supportEmailIntent.setData(Uri.parse("mailto:"));
    supportEmailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { configuration.supportEmail });
    supportEmailIntent.putExtra(Intent.EXTRA_SUBJECT, configuration.supportEmailSubject);
    supportEmailIntent.putExtra(Intent.EXTRA_TEXT, configuration.supportEmailHeader + getResources().getString(R.string.smartapprating_email_footer, configuration.versionName, VERSION.RELEASE, Build.MODEL, getConnectivityNetworkType()));
    return supportEmailIntent;
  }

  @NonNull
  protected final String getConnectivityNetworkType()
  {
    final ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

    if (connectivityManager == null)
    {
      return "?"; //not connected
    }

    final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
    if (networkInfo == null || !networkInfo.isConnected())
    {
      return "?"; //not connected
    }

    if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI)
    {
      return "WIFI";
    }

    if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE)
    {
      final int networkType = networkInfo.getSubtype();
      switch (networkType)
      {
        case TelephonyManager.NETWORK_TYPE_GPRS:
        case TelephonyManager.NETWORK_TYPE_EDGE:
        case TelephonyManager.NETWORK_TYPE_CDMA:
        case TelephonyManager.NETWORK_TYPE_1xRTT:
        case TelephonyManager.NETWORK_TYPE_IDEN:
          return "2G";
        case TelephonyManager.NETWORK_TYPE_UMTS:
        case TelephonyManager.NETWORK_TYPE_EVDO_0:
        case TelephonyManager.NETWORK_TYPE_EVDO_A:
        case TelephonyManager.NETWORK_TYPE_HSDPA:
        case TelephonyManager.NETWORK_TYPE_HSUPA:
        case TelephonyManager.NETWORK_TYPE_HSPA:
        case TelephonyManager.NETWORK_TYPE_EVDO_B:
        case TelephonyManager.NETWORK_TYPE_EHRPD:
        case TelephonyManager.NETWORK_TYPE_HSPAP:
          return "3G";
        case TelephonyManager.NETWORK_TYPE_LTE:
          return "4G";
        default:
          return "?";
      }
    }
    return "?";
  }

  @Override
  protected void onPause()
  {
    super.onPause();
    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
  }

  protected void askLater()
  {
    final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    SmartAppRatingManager.setRateLaterTimestamp(sharedPreferences, System.currentTimeMillis());
    SmartAppRatingManager.increaseNumberOfTimeLaterWasClicked(sharedPreferences);
    finish();
  }

  protected void setFirstScreenTitle(@Nullable final String titleFromConfig)
  {
    setText(title, titleFromConfig);
  }

  protected void setFirstScreenLaterButtonText(@Nullable final String laterButtonTextFromConfig)
  {
    setText(later, laterButtonTextFromConfig);
  }

  protected void setFirstScreenParagraph(@Nullable final String contentFromConfig)
  {
    setText(paragraph, contentFromConfig);
  }

  protected void setSecondScreenTitle(@Nullable final String titleFromConfig)
  {
    setText(dislikeTitle, titleFromConfig);
  }

  protected void setSecondScreenParagraph(@Nullable final String laterButtonTextFromConfig)
  {
    setText(dislikeParagraph, laterButtonTextFromConfig);
  }

  protected void setSecondScreenActionButtonText(@Nullable final String contentFromConfig)
  {
    setText(dislikeActionButton, contentFromConfig);
  }

  protected void setSecondScreenLaterButtonText(@Nullable final String contentFromConfig)
  {
    setText(dislikeExitButton, contentFromConfig);
  }

  protected final void setText(@NonNull TextView textView, final String text)
  {
    if (TextUtils.isEmpty(text) == false)
    {
      textView.setText(text);
    }
  }

  protected Bundle generateAnalyticsExtraInfos()
  {
    final Bundle extraInfos = new Bundle();

    extraInfos.putLong("sendNumber", getNumberOfReminderAlreadySeen());
    extraInfos.putInt("versionCode", getVersionCode());
    extraInfos.putString("date", getDateForAnalytics());

    return extraInfos;
  }

  @Override
  public long getNumberOfReminderAlreadySeen()
  {
    final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    return SmartAppRatingManager.getNumberOfTimeLaterWasClicked(sharedPreferences);
  }
}
