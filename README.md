# SmartAppRating

Small library which can be used to display a rating popup. Different informations are used to trigger the popup such as last crash date, number of session, etc

## Usage

### JSON Configuration file

With version 1.0.0, the only way to use this library is via a JSON configuration file.
A sample is available in this repository : [here](rateConfiguration.json)

### Gradle (Internal Nexus repository)

**Gradle :**

```groovy
implementation 'com.smartnsoft:smartapprating:1.0'
```

### Application configuration

**Application class :**

```java
final SmartAppRatingManager smartAppRatingManager = new Builder(this)
        .setIsInDevelopmentMode(BuildConfig.DEBUG)
        .setApplicationId(BuildConfig.APPLICATION_ID)
        .setApplicationVersionName(BuildConfig.VERSION_NAME)
        .setConfigurationFileURL("http://simple_base_url.com/", "path/to/configuration/file")
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

## 1.0.0 (2018-02-01)
* Initial release

## Author

The Android Team @ [Smart&Soft](http://www.smartnsoft.com/), software agency

## Contribution
All sorts of contributions are welcome. Please create a pull request and/or issue.
