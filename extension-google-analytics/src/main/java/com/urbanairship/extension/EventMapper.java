package com.urbanairship.extension;

import com.urbanairship.analytics.CustomEvent;

import java.util.Map;

/**
 * An interface for mapping GA event JSON to UA custom events. A default event mapper is provided,
 * however an implementer may also provide their own mapping for less general cases.
 */
public interface EventMapper {

    /**
     * Map event JSON to a custom event.
     *
     * @param json The event JSON.
     * @param tracker The GA Tracker instance.
     * @return A custom event builder.
     */
    public CustomEvent.Builder map(Map<String, String> json, com.google.android.gms.analytics.Tracker tracker);
}
