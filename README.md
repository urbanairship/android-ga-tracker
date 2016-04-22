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

## Required Dependencies

The project must already be integrated with the Urban Airship SDK version 7.1.0 or higher.

Analytics must be enabled to use this feature.

- Verify analytics is enabled

```java
    boolean analyticsEnabled = UAirship.shared().getAnalytics().isEnabled();
```

- Enable analytics, if it isn't already enabled

```java
    UAirship.shared().getAnalytics().setEnabled(true);
```

## Example

Using the GoogleAnalyticsTracker:

```java
// First create a Google Analytics Tracker
GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
tracker = analytics.newTracker("trackingId");

// Then wrap the Tracker in a GoogleAnalyticsTracker instance
uaTracker = new GoogleAnalyticsTracker(mTracker);

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
