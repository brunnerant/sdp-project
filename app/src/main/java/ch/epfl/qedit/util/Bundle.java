package ch.epfl.qedit.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Bundle {
    private final Map<String, Object> attributes;

    public Bundle() {
        this.attributes = Collections.emptyMap();
    }

    public Bundle(Map<String, Object> attributes) {
        this.attributes = new HashMap<>(attributes);
    }

    public Object get(String attribute) {
        return attributes.get(attribute);
    }

    public Bundle update(String attribute, Object value) {
        attributes.put(attribute, value);
        return this;
    }

    public Bundle update(String attribute, Bundle bundle) {
        attributes.put(attribute, bundle.attributes);
        return this;
    }
}
