package ch.epfl.qedit.search;

import java.util.Map;

public class SearchableMapEntry extends SearchableList<Map.Entry<String, String>> {

    @Override
    public Map.Entry<String, String> search(String string, int position) {
        if (position >= e.size()) {
            throw new IllegalArgumentException();
        }

        e.get(position);
        if (e.get(position).getKey().toLowerCase().contains(string)
                || e.get(position).getValue().toLowerCase().contains(string)) {
            return e.get(position);
        }
        return null;
    }
}
