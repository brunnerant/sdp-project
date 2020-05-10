package ch.epfl.qedit.util;

import android.content.Context;
import android.location.Criteria;
import android.location.LocationManager;

import ch.epfl.qedit.backend.location.MockLocService;

public class LocationHelper {
    public static void initLocationProvider(Context context) {
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(MockLocService.LOCATION_PROVIDER)) {
            manager.addTestProvider(MockLocService.LOCATION_PROVIDER, false, false, false, false, false, false, false, Criteria.POWER_LOW, Criteria.ACCURACY_FINE);
            manager.setTestProviderEnabled(MockLocService.LOCATION_PROVIDER, true);
        }
    }

    public static void cleanupLocationProvider(Context context) {
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        manager.removeTestProvider(MockLocService.LOCATION_PROVIDER);
    }
}
