package ch.epfl.qedit.Search;

import android.util.Pair;

import java.util.ArrayList;

public interface Searchable<T> {
    /**
     *
     * @param string the string to be searched (e.g string)
     * @param position the position search
     * @return the found T
     */
    T search(String string, int position);
}