package ch.epfl.qedit.backend.location;

import android.content.Context;
import java.util.function.Function;

/**
 * This factory class allows the frontend classes to retrieve the singleton authentication service
 * without caring about its implementation.
 */
public final class LocServiceFactory {
    /** The singleton instance of the auth service */
    private static LocationService locService = null;

    /** The callback used to instantiate a new instance, once the context was aquired */
    private static Function<Context, LocationService> builder = null;

    public static LocationService getInstance(Context context) {
        if (locService == null) {
            if (builder == null) locService = new AndroidLocService(context);
            else locService = builder.apply(context);
        }

        return locService;
    }

    /**
     * This function sets the instance of the location service, useful for mocking it in tests. It
     * has to take a callback, because the instance needs to be built using the application context,
     * but this method cannot be called once the context was built. To see this in use, have a look
     * at QuestionLocatorActivityTest.
     *
     * @param builder the builder used to create the location service.
     */
    public static void setInstance(Function<Context, LocationService> builder) {
        LocServiceFactory.builder = builder;
    }
}
