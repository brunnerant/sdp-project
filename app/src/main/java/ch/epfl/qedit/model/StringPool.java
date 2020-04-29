package ch.epfl.qedit.model;

import com.google.common.collect.ImmutableMap;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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

    private String newUID() {
        String id = UUID.randomUUID().toString();
        while (stringPool.containsKey(id)) {
            id = UUID.randomUUID().toString();
        }
        return id;
    }

    public String put(String text) {
        String id = newUID();
        stringPool.put(id, text);
        return id;
    }

    public String put(String key, String text) {
        return stringPool.put(key, text);
    }

    public String get(String key) {
        return stringPool.get(key);
    }

    public ImmutableMap<String, String> get() {
        return ImmutableMap.copyOf(stringPool);
    }
}
