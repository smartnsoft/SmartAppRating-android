# SmartAppRating

Small library which can be used to display a rating popup. Different informations are used to trigger the popup such as last crash date, number of session, etc

## Usage

### Gradle

**Gradle :**

```groovy
implementation 'com.smartnsoft:smartapprating:2.0.0'
```

### Configuration providers

There are multiple ways to configure the popup: A local configuration, a remote json or Firebase RemoteConfig.


For the local configuration, you just have to create a `Configuration` object with the required values and then give it to the builder via the `setFallbackConfiguration` method.

For the remote configuration, first you must host on your server a json which look like that : ([sample](rateConfiguration.json)). Then set the factory to `JsonConfigFactory` in the builder with the right URL and path.

Finally, for Firebase RemoteConfig, you can declare each field independently or just one field containing a json. 
The parameters to declare in RemoteConfig with their respective default key are :

|              Key             	|                               Description                               	|
|:----------------------------:	|:-----------------------------------------------------------------------:	|
|        rating_disabled       	|              A quick way to enable or disable the component             	|
|  rating_displaySessionCount  	|         After how many sessions do we want to display the popup         	|
|  rating_maxNumberOfReminder  	|  How many times do we want to ask again after user used "later" button  	|
|    rating_daysWithoutCrash   	|              How long after a crash can we ask for a rating             	|
| rating_daysBeforeAskingAgain 	|   How long after the last popup was displayed do we want to ask again   	|
| rating_maxDaysBetweenSession 	|         How many days can separate session before they are reset        	|
| rating_mainTitle             	| Title of the popup asking for a rating                                  	|
| rating_mainText              	| Message of the popup asking for a rating                                	|
| rating_positiveStarsLimit    	| How many stars do we want to consider the rating as positive            	|
| rating_dislikeMainTitle      	| Title of the popup asking why user gave a negative rating               	|
| rating_dislikeMainText       	| Message of the popup asking why user gave a negative rating             	|
| rating_dislikeActionButton   	| Text of the action button redirecting to the support                    	|
| rating_dislikeExitButtonText 	| Text of the later button after a negative rating                        	|
| rating_likeMainTitle         	| Title of the popup asking why user gave a positive rating               	|
| rating_likeMainText          	| Message of the popup asking why user gave a positive rating             	|
| rating_likeActionButton      	| Text of the action button redirecting to the app page on the Play Store 	|
| rating_likeExitButton        	| Text of the later button after a positive rating                        	|
| rating_emailSupport          	| Email used for negative ratings                                         	|
| rating_emailObject           	| Subject of the email for the support                                    	|
| rating_emailHeaderContent    	| Header for the support email                                            	|
| rating_config                	| Single field containing the json configuration                          	|

If you want to change the keys in Firebase, you must override them by giving a `RemoteConfigMatchingInformation` object to the factory.

### Application configuration

**Application class :**

```java
final SmartAppRatingManager ratingManager = new Builder(this)
        .setIsInDevelopmentMode(BuildConfig.DEBUG)
        .setApplicationId(BuildConfig.DEBUG ? "com.smartnsoft.metro" : BuildConfig.APPLICATION_ID)
        .setRatePopupActivity(AnimatedSmartAppRatingActivity.class)
        .setApplicationVersionName(BuildConfig.VERSION_NAME)
        .setFallbackConfiguration(new Configuration())
        .setFactory(new JsonConfigFactory("https://next.json-generator.com/", "api/json/get/4yBX9X0CN"))
        //.setFactory(new RemoteConfigFactory())
        .build();

// Needed to detect crashes
SmartAppRatingManager.setUncaughtExceptionHandler(this, Thread.getDefaultUncaughtExceptionHandler());
```
**In order to count the number of session :**

```java
SmartAppRatingManager.increaseNumberOfSession(PreferenceManager.getDefaultSharedPreferences(this));
```

**Display the popup :**

* If you want to retrieve the configuration and display the popup in one call

```java
smartAppRatingManager.fetchConfigurationAndTryToDisplayPopup();
```

* If you want to retrieve the configuration and display the popup later

```java
// first
smartAppRatingManager.fetchConfiguration();
...
// then later
smartAppRatingManager.showRatePopup();
```

**If you want to ask user to rate the app again, for example after a major update, you'll have to use this method:**

```java
SmartAppRatingManager.resetRating(sharedPreferences);
```

### Custom popup activity

If you want to use a custom layout or override specific parts of the popup activity :

* Extends from class SmartAppRatingActivity

```java
public final class MyCustomAppRatingActivity
    extends SmartAppRatingActivity
{

  @Override
  protected int getLayoutId()
  {
    return R.layout.my_custom_layout;
  }
  
}
```

* And register the new one in the SmartAppRatingManager Builder

```java
final SmartAppRatingManager smartAppRatingManager = new Builder(this)
        .setRatePopupActivity(MyCustomAppRatingActivity.class)
        ...
        .build();
```

## Releases

## 2.0.0 (2019-10-21)
* Converted a lot of files to Kotlin
* Added Local and RemoteConfig configuration support
* Each configuration support requires a specific module

## 1.2.0 (2019-06-05)
* Added the ability to set custom drawable for each position of the rating bar
* Available on Bintray !

## 1.1.0 (2018-06-05)
* Updated ConstraintLayout to version 1.1.0
* Added a way to get RatingPopup Intent to add more extras
* Improved the rating bar widget
* Disabled the reset rating feature of the SimpleRatingBar

## 1.0.0 (2018-02-01)
* Initial release

## Author

The Android Team @ [Smart&Soft](http://www.smartnsoft.com/), software agency

## Contribution
All sorts of contributions are welcome. Please create a pull request and/or issue.
