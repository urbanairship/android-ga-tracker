# Google Analytics Android Extension

An Android extension that proxies Google Analytics events as Urban Airship custom events. GoogleAnalyticsTracker will generate custom events, including all Google Analytics event fields and a pre-defined subset of Tracker fields, which may then be further customized by implementing an Extender. Any of the non-deprecated HitBuilders event types may be used, where the event type will be set as the custom event name.


## Installation

ga-tracker is available through [Bintray](https://bintray.com/urbanairship). To install
it, simply add the following lines to your build.gradle:

```java
repositories {
   maven {
      url  "https://urbanairship.bintray.com/android"
   }
}

dependencies {
   compile 'com.urbanairship.android:ga-tracker:1.0.+'
}
```

## Usage

Following the pattern provided in the [Google Analytics guide](https://developers.google.com/analytics/devguides/collection/android/v4/#set-up-your-project),
first create a GoogleAnalyticsTracker instance in your Application class

```java

public class YourApplication extends Application {
  private GoogleAnalyticsTracker uaTracker;

  synchronized public GoogleAnalyticsTracker getDefaultTracker() {
    if (uaTracker == null) {
      // Create a Tracker instance and wrap it in a GoogleAnalyticsTracker instance
      GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
      Tracker tracker = analytics.newTracker(R.xml.global_tracker);
      uaTracker = new GoogleAnalyticsTracker(tracker);

      // The proxy settings can be easily configured to prevent forwarding events to either Urban Airship or Google Analytics.
      // Enable GA tracker (enabled by default)
      uaTracker.setGoogleAnalyticsEnabled(true);

      // Enable UA tracker (enabled by default)
      uaTracker.setUrbanAirshipEnabled(true);

      // Add an Extender to add properties to the generated customEvent
      Extender extender = new Extender() {
          @Override
          public void extend(CustomEvent.Builder builder, Map<String, String> json, GoogleAnalyticsTracker tracker) {
              builder.addProperty("propertyName", "propertyValue");
          }
      };
      uaTracker.addExtender(extender);

    }
    return uaTracker;
  }
```

In your Activity or Fragment, you can then retrieve the tracker to create events

```java

// Obtain the shared Tracker instance.
AnalyticsApplication application = (AnalyticsApplication) getApplication();
uaTracker = application.getDefaultTracker();

// Create events
uaTracker.setScreenName("HomeScreen");
uaTracker.send(new HitBuilders.ScreenViewBuilder().build());
uaTracker.send(new HitBuilders.EventBuilder().setAction("action").setCategory("category").setLabel("label").setValue(5).build());
uaTracker.send(new HitBuilders.TimingBuilder().setCategory("category").setLabel("label").setValue(5).setVariable("variable").build());
uaTracker.send(new HitBuilders.ExceptionBuilder().setFatal(false).setDescription("description").build());
uaTracker.send(new HitBuilders.SocialBuilder().setTarget("target").setNetwork("network").setAction("action").build());
```

##Contributing Code

We accept pull requests! If you would like to submit a pull request, please fill out and submit a Code Contribution Agreement (http://docs.urbanairship.com/contribution-agreement.html).
