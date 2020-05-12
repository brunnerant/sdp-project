package ch.epfl.qedit.backend.location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;
import androidx.core.app.ActivityCompat;

public class AndroidLocService implements LocationService {

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

        // If the application doesn't have the permission, we cannot subscribe
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                                context, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) return false;

        // Otherwise, we subscribe to the location service
        manager.requestLocationUpdates(interval, 0, criteria, listener, null);
        return true;
    }

    @Override
    public void unsubscribe(LocationListener listener) {
        manager.removeUpdates(listener);
    }
}
