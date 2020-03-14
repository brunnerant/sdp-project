package ch.epfl.qedit.backend.database;

import ch.epfl.qedit.util.Bundlable;
import ch.epfl.qedit.util.Bundle;
import ch.epfl.qedit.util.Callback;

/**
 * This interface represents a database from which data can be retrieved or modified.
 * For now, only getting data is possible, but in the future, there will be support for modifying
 * data.
 */
public interface DatabaseService {
    class DatabaseResponse<T> {
        public enum Error {
            ConnectionError, IllegalFormat,
            CollectionNotFound, DocumentNotFound
        }

        private final T data;
        private final Error error;

        private DatabaseResponse(T data, Error error) {
            this.data = data;
            this.error = error;
        }

        public static <T> DatabaseResponse<T> ok(T data) {
            return new DatabaseResponse<>(data, null);
        }

        public static <T> DatabaseResponse<T> error(Error error) {
            return new DatabaseResponse<>(null, error);
        }

        public boolean successful() {
            return error == null;
        }

        public T getData() {
            return data;
        }

        public Error getError() {
            return error;
        }
    }

    /**
     * Asynchronously retrieves data from the database. Note that we assume for convenience
     * that that database model is key-based, because it is the case for Firestore.
     * @param collection the collection from which to retrieve data
     * @param document the document to retrieve in the collection
     * @param onReceive the callback that will be triggered when the data arrives
     */
    void getBundle(String collection, String document, Callback<DatabaseResponse<Bundle>> onReceive);
}
