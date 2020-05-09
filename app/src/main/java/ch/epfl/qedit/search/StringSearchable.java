package ch.epfl.qedit.search;

public class StringSearchable extends SearchableList<String> {
    @Override
    public String search(String string, int position) {
        if (position >= e.size()) {
            throw new IllegalArgumentException();
        }

        if (e.get(position).contains(string)) {
            e.get(position);
        }

        return null;
    }
}
