package ch.epfl.qedit.search;

import java.util.Map;

public class SearchableMapEntry extends SearchableList<Map.Entry<String, String>> {

    @Override
    public Map.Entry<String, String> search(String string, int position) {
        if (position >= list.size()) {
            throw new IllegalArgumentException();
        }

        // Get element of list from super class at position "position"
        list.get(position);
        if (list.get(position).getKey().toLowerCase().contains(string)
                || list.get(position).getValue().toLowerCase().contains(string)) {
            return list.get(position);
        }
        return null;
    }
}
