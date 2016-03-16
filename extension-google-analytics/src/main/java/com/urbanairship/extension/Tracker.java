package com.urbanairship.extension;

import android.net.Uri;
import android.support.annotation.IntRange;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.urbanairship.UAirship;
import com.urbanairship.analytics.CustomEvent;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Urban Airship wrapper for the Google Analytics Tracker class.
 */
public class Tracker {

    /**
     * Proxy setting constant for sending only Google Analytics events.
     */
    public static final int GA_ENABLED = 0;

    /**
     * Proxy setting constant for sending both Google Analytics events and Urban Airship custom events.
     */
    public static final int GA_AND_UA_PROXY_ENABLED = 1;

    /**
     * Proxy setting constant for sending only Urban Airship custom events.
     */
    public static final int UA_PROXY_ENABLED = 2;

    /**
     * Default set of Tracker level fields included in custom events - includes protocol version,
     * tracking ID, Client ID, User ID.
     */
    public static final Set<String> DEFAULT_TRACKER_FIELDS = new HashSet<>(Arrays.asList("v", "tid", "cid", "uid"));

    private final int proxySetting;
    private final com.google.android.gms.analytics.Tracker tracker;
    private final Set<String> trackerFields;

    /**
     * Constructor for creating the UA Tracker wrapper.
     *
     * @param tracker The GA Tracker instance.
     */
    private Tracker(com.google.android.gms.analytics.Tracker tracker, int proxySetting, Set<String> trackerFields) {
        this.tracker = tracker;
        this.proxySetting = proxySetting;
        this.trackerFields = trackerFields;
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
     * Method to return the proxy setting.
     *
     * @return The proxy setting.
     */
    public int getProxySetting() {
        return proxySetting;
    }

    /**
     * Method to return the Tracker level fields included in the custom events.
     *
     * @return The set of field names.
     */
    public Set<String> getTrackerFields() {
        return trackerFields;
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
        String eventName = json.get("t");

        // Create a custom event builder
        CustomEvent.Builder customEvent = new CustomEvent.Builder(eventName);

        // Extract the tracker level properties
        for (String trackerField : trackerFields) {
            if (tracker.get(trackerField) != null) {
                customEvent.addProperty(trackerField, tracker.get(trackerField));
            }
        }

        // Extract event properties
        for (Map.Entry<String, String> entry : json.entrySet()) {
            customEvent.addProperty(entry.getKey(), entry.getValue());
        }

        switch (proxySetting) {
            case GA_ENABLED:
                tracker.send(json);
                break;
            case GA_AND_UA_PROXY_ENABLED:
                tracker.send(json);
                UAirship.shared().getAnalytics().addEvent(customEvent.create());
                break;
            case UA_PROXY_ENABLED:
                UAirship.shared().getAnalytics().addEvent(customEvent.create());
                break;
            default:
                tracker.send(json);
        }
    }

    public static class Builder {

        @IntRange(from=0, to=2)
        private int proxySetting = 0;
        private com.google.android.gms.analytics.Tracker tracker;
        private Set<String> trackerFields = DEFAULT_TRACKER_FIELDS;

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
         * @param configResId The config res ID.
         * @return Builder
         */
        public Builder setTracker(GoogleAnalytics analytics, int configResId) {
            this.tracker = analytics.newTracker(configResId);
            return this;
        }

        /**
         * Set the proxy setting.
         *
         * @param proxySetting The proxy setting. Possible values are:
         * <br><ul>
         * <li>GA_ENABLED
         * <li>GA_AND_UA_PROXY_ENABLED
         * <li>UA_PROXY_ENABLED
         * </ul><br>
         * Defaults to <code>GA_ENABLED</code>
         * @return Buidler
         */
        public Builder setProxySetting(int proxySetting) {
            this.proxySetting = proxySetting;
            return this;
        }

        /**
         * Set the tracker level field names to be included as custom even properties. Defaults to
         * <code>DEFAULT_TRACKER_FIELDS</code>.
         *
         * @param trackerFields The set of tracker field names.
         * @return Builder
         */
        public Builder setTrackerFields(Set<String> trackerFields) {
            this.trackerFields = trackerFields;
            return this;
        }

        /**
         * Build the UA Tracker instance.
         *
         * @return The UA Tracker instance.
         */
        public Tracker build() {
            return new Tracker(tracker, proxySetting, trackerFields);
        }
    }

}

