package com.smartnsoft.smartapprating;

import android.os.Bundle;

/**
 * @author Adrien Vitti
 * @since 2018.02.13
 */
public interface RatingScreenAnalyticsInterface
{

  void sendUserSetRating(int rating);

  void sendRatingScreenDisplay();

  void sendAskLaterClickOnRatingScreen();

  void sendRatingSuggestionOk();

  void sendRatingSuggestionLater();

  void sendRatingStoreActivate();

  void sendRatingStoreLater();

  Bundle generateAnalyticsExtraInfos();

  int getVersionCode();

  long getNumberOfReminderAlreadySeen();

  String getDateForAnalytics();

  void sendAnalyticsEvent(final String eventName, final Bundle bundle);

}
