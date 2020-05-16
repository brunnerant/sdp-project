package ch.epfl.qedit.backend.location;

import android.Manifest;
import android.content.Context;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;
import ch.epfl.qedit.backend.permission.PermManagerFactory;
import ch.epfl.qedit.backend.permission.PermissionManager;

public class AndroidLocService implements LocationService {

    // This the minimum distance at which location updates are received
    private final int MIN_DISTANCE = 5;

    private final Context context;
    private final LocationManager manager;

    public AndroidLocService(Context context) {
        this.context = context;
        this.manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public boolean subscribe(LocationListener listener, int interval) {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(true);
        criteria.setCostAllowed(false);
        criteria.setSpeedRequired(false);

        // We need the permission manager to check the location permissions
        PermissionManager permManager = PermManagerFactory.getInstance();

        if (!permManager.checkPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                || !permManager.checkPermission(
                        context, Manifest.permission.ACCESS_COARSE_LOCATION)) return false;

        // Otherwise, we subscribe to the location service
        manager.requestLocationUpdates(interval, MIN_DISTANCE, criteria, listener, null);
        return true;
    }

    @Override
    public void unsubscribe(LocationListener listener) {
        manager.removeUpdates(listener);
    }
}
