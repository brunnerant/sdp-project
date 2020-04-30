package ch.epfl.qedit.backend.database;

import ch.epfl.qedit.R;
import ch.epfl.qedit.Search.Searchable;
import ch.epfl.qedit.model.Question;
import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.util.Callback;
import ch.epfl.qedit.util.Error;
import ch.epfl.qedit.util.Response;
import java.util.List;
import java.util.Map;

/**
 * This interface represents a database from which data can be retrieved or modified. For now, only
 * getting data is possible, but in the future, there will be support for modifying data.
 */
public interface DatabaseService {

    Error CONNECTION_ERROR = new Error(R.string.connection_error_message);
    Error WRONG_COLLECTION = new Error(R.string.wrong_quiz_id_message);
    Error WRONG_DOCUMENT = new Error(R.string.wrong_document_error_message);

    /**
     * Asynchronously retrieves a supported language table of a quiz from the database. Note that we
     * assume for convenience that that database model is key-based, because it is the case for
     * Firestore.
     *
     * @param quizID the id of the quiz in the database from which we retrieve the supported
     *     language table
     * @param responseCallback the callback that will be triggered when the data arrives
     */
    void getQuizLanguages(String quizID, final Callback<Response<List<String>>> responseCallback);

    /**
     * Asynchronously retrieves a string pool of a quiz from the database. Note that we assume for
     * convenience that that database model is key-based, because it is the case for Firestore.
     *
     * @param quizID the id of the quiz in the database from which we retrieve the string pool
     * @param language language code ("en", "fr", "de" ...) corresponding to which string pool we
     *     want to retrieve
     * @param responseCallback the callback that will be triggered when the data arrives
     */
    void getQuizStringPool(
            String quizID,
            String language,
            final Callback<Response<Map<String, String>>> responseCallback);

    /**
     * Asynchronously retrieves the structure of a quiz from the database. Note that we assume for
     * convenience that that database model is key-based, because it is the case for Firestore.
     *
     * @param quizID the id of the quiz in the database from which we retrieve the quiz structure
     * @param responseCallback the callback that will be triggered when the data arrives
     */
    void getQuizStructure(String quizID, final Callback<Response<Quiz>> responseCallback);

    // FOR NOW, WE KEEP getQuiz(...), getQuestions(...), getQuizTitle(...)
    // THIS WILL BE DELETED IN A FURTHER PR

    // TODO DELETE IN FURTHER PR
    // ========================================================================================

    /**
     * Asynchronously retrieves a list of question of a quiz from the database. Note that we assume
     * for convenience that that database model is key-based, because it is the case for Firestore.
     *
     * @param quizID the id of the quiz in the database from which we retrieve the questions' list.
     * @param responseCallback the callback that will be triggered when the data arrives
     */
    void getQuizQuestions(String quizID, Callback<Response<List<Question>>> responseCallback);

    /**
     * Asynchronously retrieves the title of a quiz from the database. Note that we assume for
     * convenience that that database model is key-based, because it is the case for Firestore.
     *
     * @param quizID the id of the quiz in the database from which we retrieve the title.
     * @param responseCallback the callback that will be triggered when the data arrives
     */
    void getQuizTitle(String quizID, Callback<Response<String>> responseCallback);

    /**
     * Asynchronously retrieves an entire quiz from the database. Note that we assume for
     * convenience that database model is key-based, because it is the case for Firestore.
     *
     * @param quizID the id of the quiz in the database
     * @param responseCallback the callback that will be triggered when the data arrives
     */
    void getQuiz(String quizID, Callback<Response<Quiz>> responseCallback);

    void searchDatabase(int number, String search, Callback<Response<String>> responseCallback);

    // ========================================================================================
}
