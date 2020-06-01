package ch.epfl.qedit.backend.database;

import android.content.Context;
import java.util.function.Function;

/**
 * This factory class is used to manage the singleton instance of a database service. It can be used
 * by android tests to inject mock dependencies.
 */
public final class DatabaseFactory {
    /** The singleton instance of the auth service */
    private static DatabaseService dbService = null;

    /** The callback used to instantiate a new instance, once the context was acquired */
    private static Function<Context, DatabaseService> builder = null;

    private DatabaseFactory() {}

    public static DatabaseService getInstance(Context context) {
        if (dbService == null) {
            if (builder == null) dbService = new MockDBService();
            else dbService = builder.apply(context);
        }

        return dbService;
    }

    /**
     * Sets the singleton database service. Since the context is required to build a DB service, a
     * function is passed instead of the actual service.
     *
     * @param builder the builder that will construct the service, once the context is known.
     */
    public static void setInstance(Function<Context, DatabaseService> builder) {
        DatabaseFactory.builder = builder;
    }
}
