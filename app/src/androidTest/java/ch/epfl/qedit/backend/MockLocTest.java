package ch.epfl.qedit.backend;

import static junit.framework.TestCase.fail;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import ch.epfl.qedit.backend.location.MockLocService;
import ch.epfl.qedit.util.LocationHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4ClassRunner.class)
public class MockLocTest extends LocationHelper {
    private Context context = ApplicationProvider.getApplicationContext();

    @Before
    public void init() {
        initLocationProvider(context);
    }

    @After
    public void cleanup() {
        cleanupLocationProvider(context);
    }

    @Test
    public void testListenersAreTriggered() {
        MockLocService locService = new MockLocService(context);
        locService.setLocation(0, 0);

        LocationListener listener =
                new LocationListener() {
                    private boolean called = false;

                    @Override
                    public void onLocationChanged(Location location) {
                        if (called) fail("The location listener was expected to be called once");

                        called = true;

                        if (location.getLongitude() != 11 || location.getLatitude() != 12)
                            fail("The location listener was called with the wrong location");
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {}

                    @Override
                    public void onProviderEnabled(String provider) {}

                    @Override
                    public void onProviderDisabled(String provider) {}
                };

        locService.subscribe(listener, 42);
        locService.setLocation(11, 12);
        locService.unsubscribe(listener);
        locService.setLocation(13, 14);
    }
}
