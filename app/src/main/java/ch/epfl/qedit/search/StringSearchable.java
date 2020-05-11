package ch.epfl.qedit.search;

public class StringSearchable extends SearchableList<String> {
    @Override
    public String search(String string, int position) {
        if (position >= list.size()) {
            throw new IllegalArgumentException();
        }

        if (list.get(position).contains(string)) {
            list.get(position);
        }

        return null;
    }
}
