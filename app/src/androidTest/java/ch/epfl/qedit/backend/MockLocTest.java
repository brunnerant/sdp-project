package ch.epfl.qedit.backend;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import android.Manifest;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import ch.epfl.qedit.backend.location.MockLocService;
import ch.epfl.qedit.backend.permission.MockPermManager;
import ch.epfl.qedit.backend.permission.PermManagerFactory;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;

@RunWith(AndroidJUnit4ClassRunner.class)
public class MockLocTest {
    private Context context = ApplicationProvider.getApplicationContext();

    @Test
    public void testListenersAreTriggered() {
        MockLocService locService = new MockLocService(context);
        MockPermManager permManager = new MockPermManager();
        PermManagerFactory.setInstance(permManager);

        locService.setLocation(0, 0);

        LocationListener listener = mock(LocationListener.class);

        // It should not be possible to subscribe without permission
        assertFalse(locService.subscribe(listener, 42));

        // With permissions, it should be fine
        permManager.grantPermissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION);
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

    @Test
    public void testMockMoveTo() throws InterruptedException {
        MockLocService locService = new MockLocService(context);
        locService.setLocation(0, 0);

        // We allow the permissions right away
        MockPermManager permManager = new MockPermManager();
        permManager.grantPermissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        PermManagerFactory.setInstance(permManager);

        // We subscribe
        LocationListener listener = mock(LocationListener.class);
        locService.subscribe(listener, 42);

        // We moveTo a target location
        locService.moveTo(42, 43, 2);

        // We capture the arguments of the listener, after the two seconds (plus a safety margin)
        Thread.sleep(3000);
        ArgumentCaptor<Location> locationCaptor = ArgumentCaptor.forClass(Location.class);
        verify(listener, timeout(3000).atLeast(1)).onLocationChanged(locationCaptor.capture());

        List<Location> updates = locationCaptor.getAllValues();
        Location lastLocation = updates.get(updates.size() - 1);
        assertEquals(42.0, lastLocation.getLongitude());
        assertEquals(43.0, lastLocation.getLatitude());
    }
}
