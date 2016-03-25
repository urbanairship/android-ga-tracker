package com.urbanairship.extension.analytics;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.urbanairship.UAirship;
import com.urbanairship.UrbanAirshipUtils;
import com.urbanairship.analytics.Analytics;
import com.urbanairship.analytics.CustomEvent;
import com.urbanairship.analytics.Event;
import com.urbanairship.analytics.EventTestUtils;
import com.urbanairship.extension.BuildConfig;

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

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(sdk = 21, constants = BuildConfig.class)
public class TrackerTest {

    GoogleAnalyticsTracker tracker;
    Analytics analytics;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setup() {
        UAirship airship = UrbanAirshipUtils.mockAirship();
        analytics = mock(Analytics.class);
        when(airship.getAnalytics()).thenReturn(analytics);

        tracker = new GoogleAnalyticsTracker(GoogleAnalytics.getInstance(RuntimeEnvironment.application).newTracker("trackingId"))
                .setGoogleAnalyticsEnabled(false)
                .setUrbanAirshipEnabled(true);

        tracker.setAppName("appName");
        tracker.setClientId("clientId");
    }

    @Test
    public void testScreenViewEvent() {

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                CustomEvent customEvent = (CustomEvent) invocation.getArguments()[0];
                EventTestUtils.validateEventValue(customEvent, "event_name", "screenview");
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
                EventTestUtils.validateEventValue(customEvent, "event_name", "event");
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

    @Test
    public void testSocial() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                CustomEvent customEvent = (CustomEvent) invocation.getArguments()[0];
                EventTestUtils.validateEventValue(customEvent, "event_name", "social");
                EventTestUtils.validateNestedEventValue(customEvent, "properties", "&sn", "\"network\"");
                EventTestUtils.validateNestedEventValue(customEvent, "properties", "&sa", "\"action\"");
                EventTestUtils.validateNestedEventValue(customEvent, "properties", "&st", "\"target\"");

                validateTrackerFields(customEvent);
                return null;
            }
        }).when(analytics).addEvent(any(Event.class));

        Map<String, String> event = new HitBuilders.SocialBuilder()
                .setAction("action")
                .setNetwork("network")
                .setTarget("target")
                .build();
        tracker.send(event);
    }

    @Test
    public void testTiming() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                CustomEvent customEvent = (CustomEvent) invocation.getArguments()[0];
                EventTestUtils.validateEventValue(customEvent, "event_name", "timing");
                EventTestUtils.validateNestedEventValue(customEvent, "properties", "&utc", "\"category\"");
                EventTestUtils.validateNestedEventValue(customEvent, "properties", "&utv", "\"variable\"");
                EventTestUtils.validateNestedEventValue(customEvent, "properties", "&utt", "\"5\"");
                EventTestUtils.validateNestedEventValue(customEvent, "properties", "&utl", "\"label\"");

                validateTrackerFields(customEvent);
                return null;
            }
        }).when(analytics).addEvent(any(Event.class));

        Map<String, String> event = new HitBuilders.TimingBuilder()
                .setVariable("variable")
                .setCategory("category")
                .setLabel("label")
                .setValue(5)
                .build();
        tracker.send(event);
    }

    @Test
    public void testException() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                CustomEvent customEvent = (CustomEvent) invocation.getArguments()[0];
                EventTestUtils.validateEventValue(customEvent, "event_name", "exception");
                EventTestUtils.validateNestedEventValue(customEvent, "properties", "&exd", "\"description\"");
                EventTestUtils.validateNestedEventValue(customEvent, "properties", "&exf", "\"0\"");

                validateTrackerFields(customEvent);
                return null;
            }
        }).when(analytics).addEvent(any(Event.class));

        Map<String, String> event = new HitBuilders.ExceptionBuilder()
                .setDescription("description")
                .setFatal(false)
                .build();
        tracker.send(event);
    }

    @Test
    public void testExtender() {

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                CustomEvent customEvent = (CustomEvent) invocation.getArguments()[0];
                EventTestUtils.validateEventValue(customEvent, "event_name", "screenview");
                EventTestUtils.validateNestedEventValue(customEvent, "properties", "&cd", "\"screenName\"");
                EventTestUtils.validateNestedEventValue(customEvent, "properties", "&vp", "\"viewportSize\"");
                validateTrackerFields(customEvent);
                return null;
            }
        }).when(analytics).addEvent(any(Event.class));

        tracker.setScreenName("screenName");
        Map<String, String> event = new HitBuilders.ScreenViewBuilder().build();

        tracker.setViewportSize("viewportSize");
        tracker.addExtender(new GoogleAnalyticsTracker.Extender() {
            @Override
            public void extend(CustomEvent.Builder builder, Map<String, String> json, GoogleAnalyticsTracker tracker) {
                builder.addProperty("&vp", tracker.get("&vp"));
            }
        });
        tracker.send(event);
    }

    private void validateTrackerFields(CustomEvent customEvent) throws Exception {
        EventTestUtils.validateNestedEventValue(customEvent, "properties", "&cid", "\"clientId\"");
        EventTestUtils.validateNestedEventValue(customEvent, "properties", "&an", "\"appName\"");
        EventTestUtils.validateNestedEventValue(customEvent, "properties", "&tid", "\"trackingId\"");
        assertTrue(EventTestUtils.getEventData(customEvent).getJSONObject("properties").opt("&uid") == null);
    }
}
