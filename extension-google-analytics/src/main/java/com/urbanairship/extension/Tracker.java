package com.urbanairship.extension;

import android.net.Uri;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.urbanairship.UAirship;
import com.urbanairship.analytics.CustomEvent;

import java.util.Map;

/**
 * Urban Airship wrapper for the Google Analytics Tracker class.
 */
public class Tracker {

    private final com.google.android.gms.analytics.Tracker tracker;
    private final TrackerConfiguration configuration;

    /**
     * Factory method to create a new UA Tracker with default the default configuration.
     *
     * @param analytics The GoogleAnalytics instance.
     * @param trackingId The tracking ID.
     * @return A new Tracker instance.
     */
    public static Tracker newTracker(GoogleAnalytics analytics, String trackingId) {
        return newBuilder().setTracker(analytics, trackingId).build();
    }

    /**
     * Factory method to create a new UA Tracker with default the default configuration.
     *
     * @param analytics The GoogleAnalytics instance.
     * @param configResId The config resource ID.
     * @return A new Tracker instance.
     */
    public static Tracker newTracker(GoogleAnalytics analytics, int configResId) {
        return newBuilder().setTracker(analytics, configResId).build();
    }

    /**
     * Constructor for creating the UA Tracker wrapper.
     *
     * @param tracker The GA Tracker instance.
     */
    private Tracker(com.google.android.gms.analytics.Tracker tracker, TrackerConfiguration configuration) {
        this.tracker = tracker;
        this.configuration = configuration;
    }

    /**
     * Method to create a new UA Tracker Builder.
     *
     * @return Builder
     */
    public static Builder newBuilder() {
        return new Builder();
    }

    /**
     * Method to return the GA Tracker instance.
     *
     * @return The GA Tracker instance.
     */
    public com.google.android.gms.analytics.Tracker getGaTracker() {
        return tracker;
    }

    /**
     * Method to return the TrackerConfiguration instance.
     *
     * @return The TrackerConfiguration instance.
     */
    public TrackerConfiguration getTrackerConfiguration() {
        return configuration;
    }


    // GA Tracker methods
    public void enableAdvertisingIdCollection(boolean enabled) {
        tracker.enableAdvertisingIdCollection(enabled);
    }

    public void enableAutoActivityTracking(boolean enabled) {
        tracker.enableAutoActivityTracking(enabled);
    }

    void enableExceptionReporting(boolean enable) {
        tracker.enableExceptionReporting(enable);
    }

    public String get(String key) {
        return tracker.get(key);
    }

    public void set(String key, String value) {
        tracker.set(key, value);
    }

    public void setAnonymizeIp(boolean anonymize) {
        tracker.setAnonymizeIp(anonymize);
    }

    public void setAppId(String appId) {
        tracker.setAppId(appId);
    }

    public void setAppInstallerId(String appInstallerId) {
        tracker.setAppInstallerId(appInstallerId);
    }

    public void setAppName(String appName) {
        tracker.setAppName(appName);
    }

    public void setAppVersion(String appVersion) {
        tracker.setAppVersion(appVersion);
    }

    public void setCampaignParamsOnNextHit(Uri uri) {
        tracker.setCampaignParamsOnNextHit(uri);
    }

    public void setClientId(String clientId) {
        tracker.setClientId(clientId);
    }

    public void setEncoding(String encoding) {
        tracker.setEncoding(encoding);
    }

    public void setHostname(String hostname) {
        tracker.setHostname(hostname);
    }

    public void setLanguage(String language) {
        tracker.setLanguage(language);
    }

    public void setLocation(String location) {
        tracker.setLocation(location);
    }

    public void setPage(String page) {
        tracker.setPage(page);
    }

     public void setReferrer(String referrer) {
         tracker.setReferrer(referrer);
     }

     public void setSampleRate(double sampleRate) {
         tracker.setSampleRate(sampleRate);
     }

     public void setScreenColors(String screenColors) {
         tracker.setScreenColors(screenColors);
     }

     public void setScreenName(String screenName) {
         tracker.setScreenName(screenName);
     }

     public void setScreenResolution(int width, int height) {
         tracker.setScreenResolution(width, height);
     }

     public void setSessionTimeout(long sessionTimeout) {
         tracker.setSessionTimeout(sessionTimeout);
     }

     public void setTitle(String title) {
         tracker.setTitle(title);
     }

     public void setUseSecure(boolean useSecure) {
         tracker.setUseSecure(useSecure);
     }

     public void setViewportSize(String viewportSize) {
         tracker.setViewportSize(viewportSize);
     }


    /**
     * Method to send the GA event payload. If UA proxying is enabled, a UA custom event will be created and dispatched.
     * The Custom Event will be named after the hit type with the hit event fields as properties.
     * The tracker level field names provided will then be used to include the correlated values from
     * the GA Tracker in the Custom Event. See https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters
     * for possible fields.
     *
     * @param json The GA event json.
     */
    public void send(Map<String, String> json) {

        // Extract the GA event type
        String eventType = json.get("t");

        // Create a custom event builder
        CustomEvent.Builder customEvent = new CustomEvent.Builder(eventType);

        // Extract the tracker level properties
        for (String trackerField : configuration.getAllowedTrackerFields()) {
            if (tracker.get(trackerField) != null) {
                customEvent.addProperty(trackerField, tracker.get(trackerField));
            }
        }

        // Extract event properties
        for (Map.Entry<String, String> entry : json.entrySet()) {
            customEvent.addProperty(entry.getKey(), entry.getValue());
        }

        switch (configuration.getProxySetting()) {
            case TrackerConfiguration.GA_ENABLED:
                tracker.send(json);
                break;
            case TrackerConfiguration.GA_AND_UA_PROXY_ENABLED:
                tracker.send(json);
                if (configuration.getAllowedHitTypes().contains(eventType)) {
                    UAirship.shared().getAnalytics().addEvent(customEvent.create());
                }
                break;
            case TrackerConfiguration.UA_PROXY_ENABLED:
                if (configuration.getAllowedHitTypes().contains(eventType)) {
                    UAirship.shared().getAnalytics().addEvent(customEvent.create());
                }
                break;
            default:
                tracker.send(json);
        }
    }

    public static class Builder {

        private com.google.android.gms.analytics.Tracker tracker;
        private TrackerConfiguration configuration = TrackerConfiguration.newBuilder()
                .setProxySetting(TrackerConfiguration.GA_AND_UA_PROXY_ENABLED)
                .setAllowedTrackerFields(TrackerConfiguration.DEFAULT_ALLOWED_TRACKER_FIELDS)
                .setAllowedHitTypes(TrackerConfiguration.DEFAULT_ALLOWED_HIT_TYPES)
                .build();

        private Builder() {}

        /**
         * Set the GA Tracker.
         *
         * @param tracker The Tracker instance.
         * @return Builder
         */
        public Builder setTracker(com.google.android.gms.analytics.Tracker tracker) {
            this.tracker = tracker;
            return this;
        }

        /**
         * Set the GA Tracker with a new instance.
         *
         * @param analytics The GoogleAnalytics instance.
         * @param trackingId The tacking ID.
         * @return Builder
         */
        public Builder setTracker(GoogleAnalytics analytics, String trackingId) {
            this.tracker = analytics.newTracker(trackingId);
            return this;
        }

        /**
         * Set the GA Tracker with a new instance.
         *
         * @param analytics The GoogleAnalytics instance.
         * @param configResId The config resource ID.
         * @return Builder
         */
        public Builder setTracker(GoogleAnalytics analytics, int configResId) {
            this.tracker = analytics.newTracker(configResId);
            return this;
        }

        /**
         * Set the TrackerConfiguration instance. Defaults to an instance with default field settings.
         *
         * @param configuration The TrackerConfiguration instance.
         * @return Builder
         */
        public Builder setTrackerConfiguration(TrackerConfiguration configuration) {
            this.configuration = configuration;
            return this;
        }

        /**
         * Build the UA Tracker instance.
         *
         * @return The UA Tracker instance.
         */
        public Tracker build() {
            return new Tracker(tracker, configuration);
        }
    }

}

