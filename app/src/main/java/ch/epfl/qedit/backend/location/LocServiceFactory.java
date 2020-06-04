package ch.epfl.qedit.backend.location;

import android.content.Context;
import java.util.function.Function;

/**
 * This factory class can be used to retrieve the singleton location service. Tests can also set the
 * location service, so that they can provide a mock implementation of it.
 */
public final class LocServiceFactory {
    /** The singleton instance of the location service */
    private static LocationService locService = null;

    /** The callback used to instantiate a new instance, once the context was acquired */
    private static Function<Context, LocationService> builder = null;

    public static LocationService getInstance(Context context) {
        if (builder != null) {
            // If the builder is not null, we have to replace the location service
            locService = builder.apply(context);
            builder = null;
        } else if (locService == null) {
            // If no location service exists, we create a mock by default
            locService = new MockLocService(context);
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
