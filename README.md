#Android-GA-Tracker

##Contributing Code

We accept pull requests! If you would like to submit a pull request, please fill out and submit a Code Contribution Agreement (http://docs.urbanairship.com/contribution-agreement.html).

## Installation

ga-tracker is available through [Bintray](https://bintray.com/urbanairship). To install
it, simply add the following lines to your build.gradle:

```groovy
repositories {
   maven {
      url  "https://urbanairship.bintray.com/android"
   }
}

dependencies {
   compile 'com.urbanairship.android:ga-tracker:1.0.+'
}```

## Usage

### Overview

The Urban Airship Google Analytics tracker consumes Google Analytics events via a GoogleAnalyticsTracker instance then uses this data to generate Custom Events. The GoogleAnalyticsTracker is configured by default to upload the generated Custom Events to Urban Airship, then forward the original event data to Google Analytics. The GoogleAnalyticsTracker generates custom events with all of the Google Analytics event fields and a pre-defined subset of Google Analytics Tracker fields that can be further customized using an Extender. The custom event name will be set as the Google Analytics event type, which maybe be any of the non-deprecated HitBuilders events.

### Examples

Creating the GoogleAnalyticsTracker:

```java
// First create a Google Analytics Tracker
GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
tracker = analytics.newTracker("trackingId");

// Then wrap the Tracker in a GoogleAnalyticsTracker instance
uaTracker = new GoogleAnalyticsTracker(mTracker);

// The proxy settings can be easily configured to prevent forwarding events to either Urban Airship or Google Analytics.
uaTracker.setUrbanAirshipEnabled(false).setGoogleAnalyticsEnabled(false);

```

Creating events:

```java

// Initialize a Google Analytics tracker
GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
tracker = analytics.newTracker("trackingId");

// Initialize a Urban Airship tracker
uaTracker = new GoogleAnalyticsTracker(mTracker);

// Enable GA tracker (enabled by default)
uaTracker.setGoogleAnalyticsEnabled(true);

// Enable UA tracker (enabled by default)
uaTracker.setUrbanAirshipEnabled(true)

// Add an Extender to add properties to the generated customEvent
Extender extender = new Extender() {
    @Override
    public void extend(CustomEvent.Builder builder, Map<String, String> json, GoogleAnalyticsTracker tracker) {
        builder.addProperty("propertyName", "propertyValue");
    }
};
uaTracker.addExtender(extender);

// Create Google Analytics events with the GoogleAnalyticsTracker
uaTracker.setScreenName("HomeScreen");
uaTracker.send(new HitBuilders.ScreenViewBuilder().build());
uaTracker.send(new HitBuilders.EventBuilder().setAction("action").setCategory("category").setLabel("label").setValue(5).build());
uaTracker.send(new HitBuilders.TimingBuilder().setCategory("category").setLabel("label").setValue(5).setVariable("variable").build());
uaTracker.send(new HitBuilders.ExceptionBuilder().setFatal(false).setDescription("description").build());
uaTracker.send(new HitBuilders.SocialBuilder().setTarget("target").setNetwork("network").setAction("action").build());
```