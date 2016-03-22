package com.urbanairship.extension;

import android.support.annotation.IntRange;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Configuration class for the UA Tracker wrapper.
 */
public class TrackerConfiguration {

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
    public static final Set<String> DEFAULT_ALLOWED_TRACKER_FIELDS = new HashSet<>(Arrays.asList("v", "tid", "cid", "uid"));

    /**
     * Default set of event hit types replicated as custom events.
     */
    public static final Set<String> DEFAULT_ALLOWED_HIT_TYPES = new HashSet<>(Arrays.asList("pageview", "screenview", "event", "transaction", "item", "social", "exception", "timing"));

    private final int proxySetting;
    private final Set<String> allowedTrackerFields;
    private final Set<String> allowedHitTypes;

    private TrackerConfiguration(int proxySetting, Set<String> allowedTrackerFields, Set<String> allowedHitTypes) {
        this.proxySetting = proxySetting;
        this.allowedTrackerFields = allowedTrackerFields;
        this.allowedHitTypes = allowedHitTypes;
    }

    /**
     * Method to create a new TrackerConfiguration Builder.
     *
     * @return Builder
     */
    public static Builder newBuilder() {
        return new Builder();
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
    public Set<String> getAllowedTrackerFields() {
        return allowedTrackerFields;
    }

    /**
     * Method to return the set of event hit types allowed to be sent as custom events.
     *
     * @return The set of event types.
     */
    public Set<String> getAllowedHitTypes() {
        return allowedHitTypes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TrackerConfiguration that = (TrackerConfiguration) o;

        if (proxySetting != that.proxySetting) return false;
        if (allowedTrackerFields != null ? !allowedTrackerFields.equals(that.allowedTrackerFields) : that.allowedTrackerFields != null)
            return false;
        return !(allowedHitTypes != null ? !allowedHitTypes.equals(that.allowedHitTypes) : that.allowedHitTypes != null);

    }

    @Override
    public int hashCode() {
        int result = proxySetting;
        result = 31 * result + (allowedTrackerFields != null ? allowedTrackerFields.hashCode() : 0);
        result = 31 * result + (allowedHitTypes != null ? allowedHitTypes.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TrackerConfiguration{" +
                "proxySetting=" + proxySetting +
                ", allowedTrackerFields=" + allowedTrackerFields +
                ", allowedHitTypes=" + allowedHitTypes +
                "}";
    }

    public static class Builder {
        @IntRange(from=0, to=2)
        private int proxySetting = GA_ENABLED;
        private Set<String> allowedTrackerFields = DEFAULT_ALLOWED_TRACKER_FIELDS;
        private Set<String> allowedHitTypes = DEFAULT_ALLOWED_HIT_TYPES;

        private Builder() {}

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
         * Set the tracker level field names to be included as custom even properties. Defaults to
         * <code>DEFAULT_ALLOWED_TRACKER_FIELDS</code>.
         *
         * @param allowedTrackerFields The set of tracker field names.
         * @return Builder
         */
        public Builder setAllowedTrackerFields(Set<String> allowedTrackerFields) {
            this.allowedTrackerFields = allowedTrackerFields;
            return this;
        }

        /**
         * Set the event hit types allowed to be sent as custom events. Defaults to
         * <code>DEFAULT_ALLOWED_EVENT_TYPES</code>.
         *
         * @param allowedHitTypes The set of event types.
         * @return Builder
         */
        public Builder setAllowedHitTypes(Set<String> allowedHitTypes) {
            this.allowedHitTypes = allowedHitTypes;
            return this;
        }

        /**
         * Build the TrackerConfiguration instance.
         *
         * @return The TrackerConfiguration instance.
         */
        public TrackerConfiguration build() {
            return new TrackerConfiguration(proxySetting, allowedTrackerFields, allowedHitTypes);
        }
    }
}
