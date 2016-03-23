package com.urbanairship.extension;

import com.google.android.gms.analytics.Tracker;
import com.urbanairship.analytics.CustomEvent;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Default implementation of Factory. onCreateEvent handles mapping screenview, event, social, exception, and
 * timing GA events to custom events. onPostCreate does nothing.
 */
public class DefaultFactory implements Factory {

    /**
     * Set of event hit type fields - includes category, action, label, and value."
     */
    public static final Set<String> EVENT_FIELDS = new HashSet<>(Arrays.asList("ec", "ea", "el", "ev"));

    /**
     * Set of social hit type fields - includes network, action, target."
     */
    public static final Set<String> SOCIAL_FIELDS = new HashSet<>(Arrays.asList("sn", "sa", "st"));

    /**
     * Set of exception hit type fields - includes description and is fatal flag."
     */
    public static final Set<String> EXCEPTION_FIELDS = new HashSet<>(Arrays.asList("exd", "exf"));

    /**
     * Set of timing hit type fields - includes category, variable name, time, and label."
     */
    public static final Set<String> TIMING_FIELDS = new HashSet<>(Arrays.asList("utc", "utv", "utt", "utl"));

    /**
     * Set of Tracker level fields included in custom events - includes tracking ID, client ID, user ID,
     * campaign ID, Google AdWords ID, Google Display Ads ID.
     */
    public static final Set<String> TRACKER_FIELDS = new HashSet<>(Arrays.asList("tid", "cid", "uid", "ci", "gclid", "dclid"));

    public DefaultFactory() {}

    @Override
    public CustomEvent.Builder onCreateEvent(Map<String, String> json, Tracker tracker) {
        // Extract the GA event type
        String eventType = json.get("t");

        // Create a custom event builder
        CustomEvent.Builder customEvent = new CustomEvent.Builder(eventType);

        // Extract the tracker level properties
        for (String trackerField : TRACKER_FIELDS) {
            if (tracker.get(trackerField) != null) {
                customEvent.addProperty(trackerField, tracker.get(trackerField));
            }
        }

        // Extract event properties
        Set<String> fields = new HashSet<>();
        switch (eventType) {
            case "screenview":
                customEvent.addProperty("cd", tracker.get("cd"));
                break;
            case "event":
                fields = EVENT_FIELDS;
                break;
            case "social":
                fields = SOCIAL_FIELDS;
                break;
            case "exception":
                fields = EXCEPTION_FIELDS;
                break;
            case "timing":
                fields = TIMING_FIELDS;
                break;
            default:
                break;
        }

        for (String field : fields) {
            String value = json.get(field);
            if (value != null) {
                customEvent.addProperty(field, value);
            }
        }

        return customEvent;
    }

    @Override
    public void onPostCreate(CustomEvent.Builder customEvent, Map<String, String> json, com.google.android.gms.analytics.Tracker tracker) {}
}
