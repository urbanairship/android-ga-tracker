package com.urbanairship;

import org.mockito.Mockito;
import org.robolectric.RuntimeEnvironment;

import static org.mockito.Mockito.mock;

public class UrbanAirshipUtils {

    public static UAirship mockAirship() {
        UAirship.application = RuntimeEnvironment.application;
        UAirship.isFlying = true;
        UAirship.isTakingOff = false;
        UAirship.sharedAirship = mock(UAirship.class, Mockito.RETURNS_MOCKS);

        return  UAirship.sharedAirship;
    }
}
