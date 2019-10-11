package com.smartnsoft.smartapprating;

import android.os.Bundle;

/**
 *
 * @author Adrien Vitti
 * @since 2018.02.13
 */

public final class SmartAppRatingActivity
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

}
