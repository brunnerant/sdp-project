package ch.epfl.qedit.backend.database;

/**
 * This factory class is used to manage the singleton instance of a database service. It can be used
 * by android tests to inject mock dependencies.
 */
public final class DatabaseFactory {
    /** The singleton instance of the auth service */
    private static DatabaseService dbService = null;

    private DatabaseFactory() {}

    public static DatabaseService getInstance() {
        if (dbService == null) {
            dbService = new MockDBService();
        }

        return dbService;
    }

    public static void setInstance(DatabaseService service) {
        dbService = service;
    }
}
