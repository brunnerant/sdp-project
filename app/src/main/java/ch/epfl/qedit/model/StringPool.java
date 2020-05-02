package ch.epfl.qedit.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** A String Pool containing mapping from ID to String values */
public class StringPool implements Serializable {

    public static final String TITLE_ID = "title";
    public static final String NO_QUESTION_TITLE_ID = "noQuestionTitle";
    public static final String NO_QUESTION_TEXT_ID = "noQuestionText";

    private Map<String, String> stringPool;

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
     * Put a new value inside the string pull
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

    /** Simply get the String value that map from 'id' in the String Pool */
    public String get(String id) {
        return stringPool.get(id);
    }
}
