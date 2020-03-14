package ch.epfl.qedit.backend.database;

import java.util.HashMap;
import java.util.Map;

import ch.epfl.qedit.util.Bundle;
import ch.epfl.qedit.util.Callback;

public class MockDatabaseService implements DatabaseService {

    private final Map<String, Bundle> database;

    public MockDatabaseService() {
        database = new HashMap<>();
    }

    @Override
    public void getBundle(String collection, String document, Callback<DatabaseResponse<Bundle>> onReceive) {

    }
}
