package com.smartnsoft.smartapprating;

/**
 *
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

  void generateRatingAnalyticsExtraInfos();

  int getVersionCode();

  long getNumberOfReminderAlreadySeen();

  String getDateForAnalytics();

}
