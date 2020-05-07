package ch.epfl.qedit.Search;

public class StringSearchable extends SearchablePair<String> {
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
