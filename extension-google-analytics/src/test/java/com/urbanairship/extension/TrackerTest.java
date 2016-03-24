package com.urbanairship.extension;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.urbanairship.UAirship;
import com.urbanairship.UrbanAirshipUtils;
import com.urbanairship.analytics.Analytics;
import com.urbanairship.analytics.CustomEvent;
import com.urbanairship.analytics.Event;
import com.urbanairship.analytics.EventTestUtils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricGradleTestRunner.class)
@Config(sdk = 21, constants = BuildConfig.class)
public class TrackerTest {

    Tracker tracker;
    Analytics analytics;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setup() {
        UAirship airship = UrbanAirshipUtils.mockAirship();
        analytics = mock(Analytics.class);
        when(airship.getAnalytics()).thenReturn(analytics);

        tracker = new Tracker(GoogleAnalytics.getInstance(RuntimeEnvironment.application).newTracker("trackingId"))
                .setGaEnabled(false)
                .setUaEnabled(true);

        tracker.setAppName("appName");
        tracker.setClientId("clientId");
    }

    @Test
    public void testScreenViewEvent() {

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                CustomEvent customEvent = (CustomEvent) invocation.getArguments()[0];
                EventTestUtils.validateNestedEventValue(customEvent, "properties", "&cd", "\"screenName\"");
                validateTrackerFields(customEvent);
                return null;
            }
        }).when(analytics).addEvent(any(Event.class));

        tracker.setScreenName("screenName");
        Map<String, String> event = new HitBuilders.ScreenViewBuilder().build();
        tracker.send(event);

    }

    @Test
    public void testEvent() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                CustomEvent customEvent = (CustomEvent) invocation.getArguments()[0];
                EventTestUtils.validateNestedEventValue(customEvent, "properties", "&ec", "\"category\"");
                EventTestUtils.validateNestedEventValue(customEvent, "properties", "&ea", "\"action\"");
                EventTestUtils.validateNestedEventValue(customEvent, "properties", "&el", "\"label\"");
                EventTestUtils.validateNestedEventValue(customEvent, "properties", "&ev", "\"5\"");

                        validateTrackerFields(customEvent);
                return null;
            }
        }).when(analytics).addEvent(any(Event.class));

        Map<String, String> event = new HitBuilders.EventBuilder()
                .setValue(5)
                .setLabel("label")
                .setCategory("category")
                .setAction("action")
                .build();
        tracker.send(event);
    }

    private void validateTrackerFields(CustomEvent customEvent) throws Exception {
        EventTestUtils.validateNestedEventValue(customEvent, "properties", "&cid", "\"clientId\"");
        EventTestUtils.validateNestedEventValue(customEvent, "properties", "&an", "\"appName\"");
        EventTestUtils.validateNestedEventValue(customEvent, "properties", "&tid", "\"trackingId\"");
        assertTrue(EventTestUtils.getEventData(customEvent).getJSONObject("properties").opt("&uid") == null);
    }
}
