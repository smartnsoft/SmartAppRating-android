package com.smartnsoft.smartappratingsample;

import android.animation.ObjectAnimator;
import android.os.Bundle;

import com.smartnsoft.smartapprating.AbstractSmartAppRatingActivity;
import com.smartnsoft.smartapprating.bo.Configuration;

/**
 * A simple class extending AbstractSmartAppRatingActivity to demonstrate how one can add a simple animation
 * at the view startup.
 *
 * @author David Fournier
 * @since 2018.05.03
 */
public class AnimatedSmartAppRatingActivity
    extends AbstractSmartAppRatingActivity
{

  @Override
  public int getVersionCode()
  {
    return 0;
  }

  @Override
  public String getDateForAnalytics()
  {
    return null;
  }

  @Override
  public void sendAnalyticsEvent(String eventName, Bundle bundle)
  {

  }

  @Override
  protected void setFirstScreenContent(Configuration configuration)
  {
    super.setFirstScreenContent(configuration);
    final ObjectAnimator anim = ObjectAnimator.ofFloat(rateBar, "rating", 5f, 1f, 5f);
    anim.setDuration(1000);
    anim.setStartDelay(500);
    anim.start();
  }
}
