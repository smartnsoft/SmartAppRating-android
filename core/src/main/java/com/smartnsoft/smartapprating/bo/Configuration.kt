package com.smartnsoft.smartapprating.bo

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable

/**
 * @author Adrien Vitti
 * @since 2018.01.29
 */
@JsonClass(generateAdapter = true)
data class Configuration(
    @Json(name = "disabled")
    var isRateAppDisabled: Boolean = false,

    @Json(name = "displaySessionCount")
    var numberOfSessionBeforeAskingToRate: Int = -1,

    @Json(name = "maxNumberOfReminder")
    var maxNumberOfReminder: Int = Configuration.MAXIMUM_NUMBER_OF_POPUP_REMINDER,

    @Json(name = "daysWithoutCrash")
    var minimumTimeGapAfterACrashInDays: Double = Configuration.MINIMUM_TIME_GAP_AFTER_A_CRASH_IN_DAYS,

    @Json(name = "daysBeforeAskingAgain")
    var minimumTimeGapBeforeAskingAgainInDays: Double = Configuration.MINIMUM_TIME_GAP_BEFORE_ASKING_AGAIN_IN_DAYS,

    @Json(name = "maxDaysBetweenSession")
    var maxDaysBetweenSession: Int = Configuration.MAXIMUM_TIME_GAP_BETWEEN_SESSION_IN_DAYS,

    // region First screen : Rate App
    @Json(name = "mainTitle")
    var ratePopupTitle: String? = null,

    @Json(name = "mainText")
    var ratePopupContent: String? = null,

    @Json(name = "positiveStarsLimit")
    var minimumNumberOfStarBeforeRedirectToStore: Int = -1,
    // endregion First screen

    // region Second screen : Negative or neutral rating
    @Json(name = "dislikeMainTitle")
    var dislikePopupTitle: String? = null,

    @Json(name = "dislikeMainText")
    var dislikePopupContent: String? = null,

    @Json(name = "dislikeActionButton")
    var dislikeActionButtonText: String? = null,

    @Json(name = "dislikeExitButton")
    var dislikeExitButtonText: String? = null,
    // endregion Second screen

    // region Third screen : Positive rating
    @Json(name = "likeMainTitle")
    var likePopupTitle: String? = null,

    @Json(name = "likeMainText")
    var likePopupContent: String? = null,

    @Json(name = "likeActionButton")
    var likeActionButtonText: String? = null,

    @Json(name = "likeExitButton")
    var likeExitButtonText: String? = null,
    // endregion Third screen

    @Json(name = "emailSupport")
    var supportEmail: String? = null,

    @Json(name = "emailObject")
    var supportEmailSubject: String? = null,

    @Json(name = "emailHeaderContent")
    var supportEmailHeader: String? = null,

    var versionName: String? = null,

    var applicationID: String? = null
) : Serializable
{

  companion object
  {

    const val MINIMUM_TIME_GAP_AFTER_A_CRASH_IN_DAYS: Double = 15.0

    const val MINIMUM_TIME_GAP_BEFORE_ASKING_AGAIN_IN_DAYS: Double = 3.0

    const val MAXIMUM_TIME_GAP_BETWEEN_SESSION_IN_DAYS = 3

    const val MAXIMUM_NUMBER_OF_POPUP_REMINDER = 3
  }

}
