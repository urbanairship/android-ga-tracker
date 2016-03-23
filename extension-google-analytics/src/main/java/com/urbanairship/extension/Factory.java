package com.urbanairship.extension;

import com.urbanairship.analytics.CustomEvent;

import java.util.Map;

/**
 * A factory interface for creating custom events from GA events. A default factory is provided,
 * however an implementer may also provide their own factory for less general cases.
 **/
public interface Factory {

    /**
     * Create a custom event from the GA event JSON and Tracker.
     *
     * @param json The event JSON.
     * @param tracker The GA Tracker instance.
     * @return A custom event builder.
     */
    CustomEvent.Builder onCreateEvent(Map<String, String> json, com.google.android.gms.analytics.Tracker tracker);

    /**
     * Edit the custom event post creation. This method has been provided in the event that an
     * implementer would like to use the default onCreateEvent, but would then like to append or
     * remove an arbitrary amount of fields to the custom event.
     *
     * @param customEvent The custom event builder.
     * @param json The event JSON.
     * @param tracker The GA Tracker instance.
     */
    void onPostCreate(CustomEvent.Builder customEvent, Map<String, String> json, com.google.android.gms.analytics.Tracker tracker);
}
