package ch.epfl.qedit.Search;

import android.util.Pair;

import java.util.ArrayList;

public interface Searchable<T> {

    /**
     * Asynchronously retrieves a supported language table of a quiz from the database. Note that we
     * assume for convenience that that database model is key-based, because it is the case for
     * Firestore.
     *
     * @param quizID the id of the quiz in the database from which we retrieve the supported
     *     language table
     * @param responseCallback the callback that will be triggered when the data arrives
     */

    /**
     *
     * @param string the string to be searched (e.g string)
     * @param position the position search
     * @return the found T
     */
    T search(String string, int position);
}