package ch.epfl.qedit.backend;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import android.content.Context;
import android.location.LocationListener;
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

        LocationListener listener = mock(LocationListener.class);

        // It should not be possible to subscribe without permission
        assertFalse(locService.subscribe(listener, 42));

        // With permissions, it should be fine
        locService.setPermission(true);
        assertTrue(locService.subscribe(listener, 42));

        // The listener should receive the first location update, but not the second
        locService.setLocation(11, 12);
        locService.unsubscribe(listener);
        locService.setLocation(13, 14);

        verify(listener).onLocationChanged(any());
        verify(listener)
                .onLocationChanged(
                        argThat(loc -> (loc.getLongitude() == 11) && (loc.getLatitude() == 12)));
    }
}
