package ch.epfl.qedit.backend.database;

/**
 * This factory class is used to create dependency injection for testing, or for switching the
 * authentication service if several are available.
 */
public final class DatabaseFactory {
    /** The singleton instance of the auth service */
    private static DatabaseService dbService = null;

    public static DatabaseService getInstance() {
        if (dbService == null) dbService = new MockDBService();

        return dbService;
    }

    public static void setInstance(DatabaseService service) {
        dbService = service;
    }
}
