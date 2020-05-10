package ch.epfl.qedit.util;

import android.content.Context;
import android.location.Criteria;
import android.location.LocationManager;
import ch.epfl.qedit.backend.location.MockLocService;

/**
 * This class is used in test cases to initialize the mock location provider. This is useful
 * to test activities that depend on the phone's location.
 */
public class LocationHelper {
    /**
     * This method must be called before launching the tests. It initializes the mock location
     * provider.
     */
    public static void initLocationProvider(Context context) {
        LocationManager manager =
                (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(MockLocService.LOCATION_PROVIDER)) {
            manager.addTestProvider(
                    MockLocService.LOCATION_PROVIDER,
                    false, // network not required
                    false, // satellite not required
                    false, // cell not required
                    false, // no monetary cost
                    false, // altitude not supported
                    false, // speed not supported
                    false, // bearing not supported
                    Criteria.POWER_LOW,
                    Criteria.ACCURACY_FINE);
            manager.setTestProviderEnabled(MockLocService.LOCATION_PROVIDER, true);
        }
    }

    /**
     * This method must be called after the tests. It removes the mock location provider
     * from the phone.
     */
    public static void cleanupLocationProvider(Context context) {
        LocationManager manager =
                (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        manager.removeTestProvider(MockLocService.LOCATION_PROVIDER);
    }
}
