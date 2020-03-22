package ch.epfl.qedit.util;

import java.util.HashMap;
import java.util.Map;

public class BundledData {
    private final Map<String, Object> attributes;

    public BundledData() {
        this.attributes = new HashMap<>();
    }

    public BundledData(Map<String, Object> attributes) {
        this.attributes = new HashMap<>(attributes);
    }

    public BundledData(String attribute, Object value) {
        this.attributes = new HashMap<>();
        this.update(attribute, value);
    }

    public Object get(String attribute) {
        if (!attributes.containsKey(attribute)) throw new IllegalArgumentException();

        return attributes.get(attribute);
    }

    public BundledData update(String attribute, Object value) {
        attributes.put(attribute, value);
        return this;
    }

    public BundledData update(String attribute, BundledData bundle) {
        attributes.put(attribute, bundle.attributes);
        return this;
    }
}
