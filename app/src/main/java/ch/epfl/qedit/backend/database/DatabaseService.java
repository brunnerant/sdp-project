package ch.epfl.qedit.backend.database;

import ch.epfl.qedit.util.BundledData;
import ch.epfl.qedit.util.Callback;
import ch.epfl.qedit.util.Response;

/**
 * This interface represents a database from which data can be retrieved or modified. For now, only
 * getting data is possible, but in the future, there will be support for modifying data.
 */
public interface DatabaseService {
    int CONNECTION_ERROR = 1;
    int WRONG_COLLECTION = 2;
    int WRONG_DOCUMENT = 3;

    /**
     * Asynchronously retrieves data from the database. Note that we assume for convenience that
     * that database model is key-based, because it is the case for Firestore.
     *
     * @param collection the collection from which to retrieve data
     * @param document the document to retrieve in the collection
     * @param responseCallback the callback that will be triggered when the data arrives
     */
    void getBundle(
            String collection, String document, Callback<Response<BundledData>> responseCallback);

    /**
     * Asynchronously retrieves a list of question of a quiz from the database. Note that we assume
     * for convenience that that database model is key-based, because it is the case for Firestore.
     *
     * @param quizID the id of the quiz in the database from which we retrieve the questions' list.
     * @param responseCallback the callback that will be triggered when the data arrives
     */
    void getQuizQuestions(String quizID, Callback<Response<BundledData>> responseCallback);

    /**
     * Asynchronously retrieves the title of a quiz from the database. Note that we assume for
     * convenience that that database model is key-based, because it is the case for Firestore.
     *
     * @param quizID the id of the quiz in the database from which we retrieve the title.
     * @param responseCallback the callback that will be triggered when the data arrives
     */
    void getQuizTitle(String quizID, Callback<Response<BundledData>> responseCallback);
}
