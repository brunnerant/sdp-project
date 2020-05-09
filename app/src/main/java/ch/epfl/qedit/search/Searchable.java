package ch.epfl.qedit.search;

// Interface to implement a function search.
public interface Searchable<T> {
    /**
     * @param string the string to be searched (e.g string)
     * @param position the position search
     * @return the found T
     */
    T search(String string, int position);
}
