package ch.epfl.qedit.backend.location;

import android.Manifest;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import ch.epfl.qedit.backend.permission.PermManagerFactory;
import ch.epfl.qedit.backend.permission.PermissionManager;
import java.util.HashSet;
import java.util.Set;

/** This class is used to write tests for the parts of the app that need the location service. */
public class MockLocService implements LocationService {
    /** This is the id of the mock location provider */
    public static final String LOCATION_PROVIDER = "mock_provider";

    // This is the set of listeners that need to be updated.
    private final Set<LocationListener> listeners;

    // This is the context of the location service. It is used to retrieve the permissions.
    private final Context context;

    // This is the current location
    private double longitude = 0;
    private double latitude = 0;

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

        if (permManager.checkPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                && permManager.checkPermission(
                        context, Manifest.permission.ACCESS_COARSE_LOCATION)) {
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
        notifyListeners(longitude, latitude);
        this.longitude = longitude;
        this.latitude = latitude;
    }

    /**
     * Moves gradually from the current location to the given location, over t seconds.
     *
     * @param longitude the target longitude
     * @param latitude the target latitude
     * @param t the time over which we move, in seconds
     */
    public void moveTo(double longitude, double latitude, double t) {
        // We give 10 updates per second (that's arbitrary)
        final long TIME_PER_UPDATE = 100;
        int steps = (int) (t * 1000 / TIME_PER_UPDATE);

        // We start from the current position
        double initLong = this.longitude;
        double initLat = this.latitude;

        new Thread(
                        () -> {
                            // We give updates to the listeners along our route
                            for (int i = 1; i <= steps; i++) {
                                try {
                                    Thread.sleep(TIME_PER_UPDATE);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                double interpolatedLong =
                                        interpolate(initLong, longitude, (double) i / steps);
                                double interpolatedLat =
                                        interpolate(initLat, latitude, (double) i / steps);
                                notifyListeners(interpolatedLong, interpolatedLat);
                            }
                        })
                .start();

        // And we finally set the final position
        this.longitude = longitude;
        this.latitude = latitude;
    }

    // Interpolates a value between two end points
    private double interpolate(double from, double to, double t) {
        return from * (1 - t) + to * t;
    }

    // Notifies the listeners about a certain position
    private void notifyListeners(double longitude, double latitude) {
        // We first build the location from the coordinates
        Location location = new Location("");
        location.setLongitude(longitude);
        location.setLatitude(latitude);

        // And then we trigger the listeners
        for (LocationListener listener : listeners) listener.onLocationChanged(location);
    }
}
