package com.smartnsoft.smartapprating.bo


/**
 *
 * @author Adrien Vitti
 * @since 2019.10.15
 */
data class RemoteConfigMatchingInformation
@JvmOverloads
constructor(
    val rateAppDisabled: String = REMOTE_CONFIG_RATING_DISABLED,
    val displaySessionCount: String = REMOTE_CONFIG_DISPLAY_SESSION_COUNT,
    val maxNumberOfReminders: String = REMOTE_CONFIG_MAX_NUMBER_REMINDERS,

    var minimumTimeGapAfterACrashInDays: String = REMOTE_CONFIG_DAYS_WITHOUT_CRASH,

    var minimumTimeGapBeforeAskingAgainInDays: String = REMOTE_CONFIG_DAYS_BEFORE_ASKING_AGAIN,

    var maxDaysBetweenSession: String = REMOTE_CONFIG_MAX_DAYS_BETWEEN_SESSION,

    // region First screen : Rate App
    var ratePopupTitle: String = REMOTE_CONFIG_MAIN_TITLE,

    var ratePopupContent: String = REMOTE_CONFIG_MAIN_MESSAGE,

    var minimumNumberOfStarBeforeRedirectToStore: String = REMOTE_CONFIG_POSITIVE_STAR_THRESHOLD,
    // endregion First screen

    // region Second screen : Negative or neutral rating
    var dislikePopupTitle: String = REMOTE_CONFIG_DISLIKE_MAIN_TITLE,

    var dislikePopupContent: String = REMOTE_CONFIG_DISLIKE_MAIN_MESSAGE,

    var dislikeActionButtonText: String = REMOTE_CONFIG_DISLIKE_ACTION_BUTTON,

    var dislikeExitButtonText: String = REMOTE_CONFIG_DISLIKE_EXIT_BUTTON,
    // endregion Second screen

    // region Third screen : Positive rating
    var likePopupTitle: String = REMOTE_CONFIG_LIKE_MAIN_TITLE,

    var likePopupContent: String = REMOTE_CONFIG_LIKE_MAIN_MESSAGE,

    var likeActionButtonText: String = REMOTE_CONFIG_LIKE_ACTION_BUTTON,

    var likeExitButtonText: String = REMOTE_CONFIG_LIKE_EXIT_BUTTON,
    // endregion Third screen

    var supportEmail: String = REMOTE_CONFIG_SUPPORT_EMAIL,

    var supportEmailSubject: String = REMOTE_CONFIG_SUPPORT_EMAIL_SUBJECT,

    var supportEmailHeader: String = REMOTE_CONFIG_SUPPORT_EMAIL_FOOTER,

    var jsonField: String = REMOTE_CONFIG_JSON_FIELD
)
{

  companion object
  {

    const val REMOTE_CONFIG_RATING_DISABLED = "rating_disabled"
    const val REMOTE_CONFIG_DISPLAY_SESSION_COUNT = "rating_displaySessionCount"
    const val REMOTE_CONFIG_MAX_NUMBER_REMINDERS: String = "rating_maxNumberOfReminder"
    const val REMOTE_CONFIG_DAYS_WITHOUT_CRASH: String = "rating_daysWithoutCrash"
    const val REMOTE_CONFIG_DAYS_BEFORE_ASKING_AGAIN: String = "rating_daysBeforeAskingAgain"
    const val REMOTE_CONFIG_MAX_DAYS_BETWEEN_SESSION: String = "rating_maxDaysBetweenSession"

    const val REMOTE_CONFIG_MAIN_TITLE: String = "rating_mainTitle"
    const val REMOTE_CONFIG_MAIN_MESSAGE: String = "rating_mainText"
    const val REMOTE_CONFIG_POSITIVE_STAR_THRESHOLD: String = "rating_positiveStarsLimit"

    const val REMOTE_CONFIG_DISLIKE_MAIN_TITLE: String = "rating_dislikeMainTitle"
    const val REMOTE_CONFIG_DISLIKE_MAIN_MESSAGE: String = "rating_dislikeMainText"
    const val REMOTE_CONFIG_DISLIKE_ACTION_BUTTON: String = "rating_dislikeActionButton"
    const val REMOTE_CONFIG_DISLIKE_EXIT_BUTTON: String = "rating_dislikeExitButtonText"

    const val REMOTE_CONFIG_LIKE_MAIN_TITLE: String = "rating_likeMainTitle"
    const val REMOTE_CONFIG_LIKE_MAIN_MESSAGE: String = "rating_likeMainText"
    const val REMOTE_CONFIG_LIKE_ACTION_BUTTON: String = "rating_likeActionButton"
    const val REMOTE_CONFIG_LIKE_EXIT_BUTTON: String = "rating_likeExitButton"

    const val REMOTE_CONFIG_SUPPORT_EMAIL: String = "rating_emailSupport"
    const val REMOTE_CONFIG_SUPPORT_EMAIL_SUBJECT: String = "rating_emailObject"
    const val REMOTE_CONFIG_SUPPORT_EMAIL_FOOTER: String = "rating_emailHeaderContent"

    const val REMOTE_CONFIG_JSON_FIELD: String = "rating_config"

  }

}
