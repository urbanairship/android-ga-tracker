package com.urbanairship.extension;

import android.net.Uri;

import com.urbanairship.analytics.CustomEvent;

import java.util.Map;

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

    private final com.google.android.gms.analytics.Tracker tracker;
    private final Factory factory;
    private final int proxySetting;

    /**
     * Factory method to create a new UA Tracker with default the default proxy setting and event factory.
     *
     * @param tracker The GA Tracker instance.
     * @return A new Tracker instance.
     */
    public static Tracker newTracker(com.google.android.gms.analytics.Tracker tracker) {
        return newBuilder().setTracker(tracker).build();
    }

    /**
     * Constructor for creating the UA Tracker wrapper.
     *
     * @param tracker The GA Tracker instance.
     * @param factory The custom event mapping factory.
     * @param proxySetting The event proxy setting.
     */
    private Tracker(com.google.android.gms.analytics.Tracker tracker, Factory factory, int proxySetting) {
        this.tracker = tracker;
        this.factory = factory;
        this.proxySetting = proxySetting;
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
     * Method to return the event JSON to custom event factory.
     *
     * @return The event factory.
     */
    public Factory getFactory() {
        return factory;
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
     * Method to send the GA event payload. If UA proxying is enabled, a UA custom event will be created and dispatched.
     * The Factory provides a method to map the event JSON and tracker fields to the custom event. If using the DefaultFactory,
     * the Custom Event will be named after the hit type with the relevant tracker and hit event fields as properties.
     * The factory can also be used to edit the custom event so that if the default event mapper is used,
     * any extra fields may still be added to or removed from the custom event.
     * See https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters
     * for possible fields.
     *
     * @param json The GA event json.
     */
    public void send(Map<String, String> json) {
        CustomEvent.Builder customEvent = factory.onCreateEvent(json, tracker);
        factory.onPostCreate(customEvent, json, tracker);

        switch (proxySetting) {
            case GA_ENABLED:
                tracker.send(json);
                break;
            case GA_AND_UA_PROXY_ENABLED:
                tracker.send(json);
                customEvent.addEvent();
                break;
            case UA_PROXY_ENABLED:
                customEvent.addEvent();
                break;
            default:
                tracker.send(json);
        }
    }

    public static class Builder {

        private com.google.android.gms.analytics.Tracker tracker;
        private Factory factory = new DefaultFactory();
        private int proxySetting = GA_AND_UA_PROXY_ENABLED;

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
         * Set the event JSON to custom event factory. Defaults to DefaultFactory.
         *
         * @param factory The event factory.
         * @return Builder
         */
        public Builder setFactory(Factory factory) {
            this.factory = factory;
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
         * Defaults to <code>GA_AND_UA_PROXY_ENABLED</code>
         * @return Buidler
         */
        public Builder setProxySetting(int proxySetting) {
            this.proxySetting = proxySetting;
            return this;
        }

        /**
         * Build the UA Tracker instance.
         *
         * @return The UA Tracker instance.
         */
        public Tracker build() {
            return new Tracker(tracker, factory, proxySetting);
        }
    }

    // ******************** GA Tracker methods ******************** //

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

