package ch.epfl.qedit.backend.location;

import android.Manifest;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import java.util.HashSet;
import java.util.Set;

import ch.epfl.qedit.backend.permission.PermManagerFactory;
import ch.epfl.qedit.backend.permission.PermissionManager;

/** This class is used to write tests for the parts of the app that need the location service. */
public class MockLocService implements LocationService {
    /** This is the id of the mock location provider */
    public static final String LOCATION_PROVIDER = "mock_provider";

    // This is the set of listeners that need to be updated.
    private final Set<LocationListener> listeners;

    // This is the context of the location service. It is used to retrieve the permissions.
    private final Context context;

    public MockLocService(Context context) {
        listeners = new HashSet<>();
        this.context = context;
    }

    @Override
    public boolean subscribe(LocationListener listener, int interval) {
        // In the mock, we will not take into account the interval, because the goal
        // is just to test how the UI responds to the location updates.

        // We need the permission manager to check the location permissions
        PermissionManager permManager = PermManagerFactory.getInstance();

        if (permManager.checkPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) &&
                permManager.checkPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            listeners.add(listener);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void unsubscribe(LocationListener listener) {
        listeners.remove(listener);
    }

    /**
     * Sets the location of the service, updating all the subscribed location listeners
     *
     * @param longitude the longitude of the new location
     * @param latitude the latitude of the new location
     */
    public void setLocation(double longitude, double latitude) {
        // We first build the location from the coordinates
        Location location = new Location("");
        location.setLongitude(longitude);
        location.setLatitude(latitude);

        // And then we trigger the listeners
        for (LocationListener listener : listeners) listener.onLocationChanged(location);
    }
}
