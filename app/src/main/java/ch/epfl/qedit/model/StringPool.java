package ch.epfl.qedit.model;

import ch.epfl.qedit.util.Mappable;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * This class is just a lightweight wrapper around a map to add support for string addition with
 * unique keys, update and retrieval.
 */
public class StringPool implements Serializable, Mappable {

    public static final String TITLE_ID = "main_title";

    private Map<String, String> stringPool;
    private String languageCode;

    public StringPool() {
        stringPool = new HashMap<>();
    }

    public StringPool(Map<String, String> stringPool) {
        this.stringPool = new HashMap<>(stringPool);
    }

    /** Create a unique id relative to the already existing id in this String Pool */
    private String newUID() {
        String id = UUID.randomUUID().toString();
        while (stringPool.containsKey(id)) {
            id = UUID.randomUUID().toString();
        }
        return id;
    }

    /**
     * Put a new value inside the string pool
     *
     * @param text the String value added to the string pool
     * @return the new UID created that map to text in this string pool
     */
    public String add(String text) {
        String id = newUID();
        stringPool.put(id, text);
        return id;
    }

    /**
     * Update an already existing String value
     *
     * @param id of the String value in the String Pool
     * @param text new String value to put in the string pull
     * @return the previous String value that mapped from 'id'
     */
    public String update(String id, String text) {
        return stringPool.put(id, text);
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
        if (!stringPool.containsKey(id)) return id;

        return stringPool.get(id);
    }

    @Override
    public Map<String, Object> toMap() {
        return new HashMap<>(stringPool);
    }

    /** Getter and setter for the language code */
    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }
}
