package ch.epfl.qedit.backend;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import android.Manifest;
import android.content.Context;
import android.location.LocationListener;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import ch.epfl.qedit.backend.location.MockLocService;
import ch.epfl.qedit.backend.permission.MockPermManager;
import ch.epfl.qedit.backend.permission.PermManagerFactory;
import org.junit.Test;
import org.junit.runner.RunWith;

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
}
