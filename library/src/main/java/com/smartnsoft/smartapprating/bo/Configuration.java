package com.smartnsoft.smartapprating.bo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Adrien Vitti
 * @since 2018.01.29
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public final class Configuration
    implements Serializable
{

  private static final int MINIMUM_TIME_GAP_AFTER_A_CRASH = 15;

  private static final int MINIMUM_TIME_GAP_BEFORE_ASKING_AGAIN = 3;

  @JsonProperty(value = "disabled")
  public boolean isRateAppDisabled = false;

  @JsonProperty(value = "displaySessionCount")
  public int numberOfSessionBeforeAskingToRate = -1;

  @JsonProperty(value = "daysWithoutCrash")
  public double minimumTimeGapAfterACrashInDays = MINIMUM_TIME_GAP_AFTER_A_CRASH;

  @JsonProperty(value = "daysBeforeAskingAgain")
  public double minimumTimeGapBeforeAskingAgainInDays = MINIMUM_TIME_GAP_BEFORE_ASKING_AGAIN;

  // region First screen : Rate App
  @JsonProperty(value = "mainTitle")
  public String ratePopupTitle;

  @JsonProperty(value = "mainText")
  public String ratePopupContent;

  @JsonProperty(value = "positiveStarsLimit")
  public int minimumNumberOfStarBeforeRedirectToStore = -1;
  // endregion First screen

  // region Second screen : Negative or neutral rating
  @JsonProperty(value = "dislikeMainTitle")
  public String dislikePopupTitle;

  @JsonProperty(value = "dislikeMainText")
  public String dislikePopupContent;

  @JsonProperty(value = "dislikeActionButton")
  public String dislikeActionButtonText;

  @JsonProperty(value = "dislikeExitButton")
  public String dislikeExitButtonText;
  // endregion Second screen

  // region Third screen : Positive rating
  @JsonProperty(value = "likeMainTitle")
  public String likePopupTitle;

  @JsonProperty(value = "likeMainText")
  public String likePopupContent;

  @JsonProperty(value = "likeActionButton")
  public String likeActionButtonText;

  @JsonProperty(value = "likeExitButton")
  public String likeExitButtonText;
  // endregion Third screen

  @JsonProperty(value = "emailSupport")
  public String supportEmail;

  @JsonProperty(value = "emailObject")
  public String supportEmailSubject;

  @JsonProperty(value = "emailHeaderContent")
  public String supportEmailHeader;

  @JsonIgnore
  public String versionName;

  @JsonIgnore
  public String applicationID;

}
