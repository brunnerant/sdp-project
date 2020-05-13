package ch.epfl.qedit.backend.location;

import android.location.LocationListener;

/**
 * This interface is used to request location updates from the phone. It is an interface, so that it
 * can be mocked for the tests.
 */
public interface LocationService {
    /**
     * Subscribes to the location service using the given location listener. The location updates
     * should be done at the given interval (in milliseconds), if possible.
     *
     * @param listener the listener that will be subscribed.
     * @param interval the interval at which updates should be received.
     * @return true if the permissions were granted for the location service, false otherwise.
     */
    boolean subscribe(LocationListener listener, int interval);

    /**
     * Unsubscribe the given listener, meaning that it will stop receiving updates.
     *
     * @param listener the location listener to unsubscribe.
     */
    void unsubscribe(LocationListener listener);
}
