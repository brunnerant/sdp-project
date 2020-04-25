package ch.epfl.qedit.model;

import java.util.Collections;
import java.util.Map;

/**
 * This class is just a lightweight wrapper around a map to add support for string lookups without
 * code duplication.
 */
public class StringPool {
    private final Map<String, String> pool;

    public StringPool(Map<String, String> pool) {
        this.pool = Collections.unmodifiableMap(pool);
    }

    /**
     * Retrieves a string in the pool by its id, and returns the string unchanged if it was not
     * found in the pool. This behaviour allows some strings to be the same for all the languages,
     * which could potentially ease the creation of quizzes.
     *
     * @param id the id of the string
     * @return the string corresponding to the given id
     */
    public String get(String id) {
        // If the pool does not contain the id, we assume that it is because the string
        // should be the same for all the languages, and so we return it as is.
        if (!pool.containsKey(id)) return id;

        return pool.get(id);
    }
}
