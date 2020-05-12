package ch.epfl.qedit.backend.location;

import android.content.Context;

/**
 * This factory class allows the frontend classes to retrieve the singleton authentication service
 * without caring about its implementation.
 */
public final class LocServiceFactory {
    /** The singleton instance of the auth service */
    private static LocationService locService = null;

    public static LocationService getInstance(Context context) {
        //        if (locService == null) locService = new MockLocService(context);
        if (locService == null) locService = new AndroidLocService(context);

        return locService;
    }

    public static void setInstance(LocationService service) {
        locService = service;
    }
}
