/*
 Copyright 2016 Urban Airship and Contributors
*/

package com.urbanairship.extension.analytics;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.analytics.Tracker;
import com.urbanairship.analytics.CustomEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Urban Airship wrapper for the Google Analytics Tracker class.
 */
public class GoogleAnalyticsTracker {

    /**
     * Set of Tracker level fields included in custom events - includes Protocol Version, App Name,
     * Tracking ID, User ID, Document Referrer, Campaign Name, Campaign Source, Campaign Medium,
     * Campaign Keyword, Campaign Content, Campaign ID, Google AdWords ID, Google Display Ads ID,
     * Document location URL, Document Host Name, Document Path, Document Title, Screen Name,
     * Application Version, Application ID, and Application Installer ID.
     *
     * Client ID is not included here due to the possible deadlock incited by its retrieval while an
     * event is being sent. If you would like to include the Client ID as a parameter, we recommend
     * retrieving the value once when the Tracker instance is created in the Application class before
     * including the field in an Extender. This will cache the value and prevent any future race conditions.
     */
    public static final List<String> TRACKER_FIELDS = Arrays.asList("&v", "&an", "&tid", "&uid",
            "&dr", "&cn", "&cs", "&cm", "&ck", "&cc", "&ci", "&gclid", "&dclid", "&dl", "&dh", "&dp",
            "&dt", "&cd", "&av", "&aid", "&aiid");

    private final Tracker tracker;
    private final Set<Extender> extenders = new HashSet<>();

    private volatile boolean googleAnalyticsEnabled = true;
    private volatile boolean urbanAirshipEnabled = true;

    /**
     * Constructor for creating the UA Tracker wrapper.
     *
     * @param tracker The GA Tracker instance.
     */
    public GoogleAnalyticsTracker(Tracker tracker) {
        this.tracker = tracker;
    }

    /**
     * Set the flag to send events to GA. Defaults to <code>true</code>.
     *
     * @param enabled The flag - set to <code>true</code> to send events to GA, or <code>false</code>
     *                to stop sending the events.
     * @return The UA Tracker instance.
     */
    public GoogleAnalyticsTracker setGoogleAnalyticsEnabled(boolean enabled) {
        googleAnalyticsEnabled = enabled;
        return this;
    }

    /**
     * Set the flag to send events to UA. Defaults to <code>false</code>.
     *
     * @param enabled The flag - set to <code>true</code> to send events to UA, or <code>false</code>
     *                to stop sending the events.
     * @return The UA Tracker instance.
     */
    public GoogleAnalyticsTracker setUrbanAirshipEnabled(boolean enabled) {
        urbanAirshipEnabled = enabled;
        return this;
    }

    /**
     * Add a custom event extender.
     *
     * @param extender The event extender.
     * @return The UA Tracker instance.
     */
    public GoogleAnalyticsTracker addExtender(Extender extender) {
        extenders.add(extender);
        return this;
    }

    /**
     * Method to return the GA Tracker instance.
     *
     * @return The GA Tracker instance.
     */
    public Tracker getTracker() {
        return tracker;
    }

    /**
     * Method to return the GA enabled flag.
     *
     * @return The GA enabled flag.
     */
    public boolean isGoogleAnalyticsEnabled() {
        return googleAnalyticsEnabled;
    }

    /**
     * Method to return the UA enabled flag.
     *
     * @return The UA enabled flag.
     */
    public boolean isUrbanAirshipEnabled() {
        return urbanAirshipEnabled;
    }

    /**
     * Method to send the GA event payload. If UA proxying is enabled, a UA custom event will be created and dispatched.
     *
     * @param json The GA event json.
     */
    public void send(final Map<String, String> json) {
        if (googleAnalyticsEnabled) {
            tracker.send(json);
        }

        if (urbanAirshipEnabled) {
            createCustomEvent(json);
        }
    }

    /**
     * Method to map the event JSON and tracker fields to the custom event. Instead of overriding this
     * method, the extenders can be used to add other fields from the event JSON or Tracker.
     * See https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters
     * for possible fields. Includes all event level fields and tracker level fields provided by
     * {@link #TRACKER_FIELDS}.
     *
     * @param json The event JSON.
     */
    protected void createCustomEvent(Map<String, String> json) {
        CustomEvent.Builder customEvent = new CustomEvent.Builder(getEventName(json));

        // Extract the tracker level properties
        for (String trackerField : TRACKER_FIELDS) {
            if (tracker.get(trackerField) != null) {
                customEvent.addProperty(trackerField, tracker.get(trackerField));
            }
        }

        // Extract all event properties
        for (Map.Entry<String, String> entry : json.entrySet()) {
            if (entry.getValue() != null) {
                customEvent.addProperty(entry.getKey(), entry.getValue());
            }
        }

        // Apply custom event extenders
        for (Extender extender : new ArrayList<>(extenders)) {
            extender.extend(customEvent, json, this);
        }

        customEvent.addEvent();
    }

    /**
     * Generates the custom event name by extracting the hit event type from the event JSON.
     *
     * @param json The event JSON.
     * @return The event name.
     */
    @NonNull
    protected String getEventName(Map<String, String> json) {
        // Extract the GA event type
        return json.get("&t");
    }

    /**
     * Interface to extend the custom event builder with more fields retrieved from the event JSON or Tracker.
     */
    public interface Extender {
        void extend(CustomEvent.Builder builder, Map<String, String> json, GoogleAnalyticsTracker tracker);
    }


    // ***************** Google Analytics Tracker methods ***************** //

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
}

