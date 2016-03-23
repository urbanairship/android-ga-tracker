package com.urbanairship.extension;

import com.urbanairship.analytics.CustomEvent;

import java.util.Map;

/**
 * An interface for editing custom events generated from the GA event JSON. This interface has been
 * provided in the event that an implementer would like to use the default event mapper, but would
 * then like to append or remove an arbitrary amount of fields to the custom event.
 */
public interface EventEditor {

    /**
     * Edit the custom event.
     *
     * @param customEvent The custom event builder.
     * @param json The event JSON.
     * @param tracker The GA Tracker instance.
     */
    public void edit(CustomEvent.Builder customEvent, Map<String, String> json, com.google.android.gms.analytics.Tracker tracker);
}
