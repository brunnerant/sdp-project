package ch.epfl.qedit.backend.database;

import ch.epfl.qedit.model.Question;
import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.model.answer.AnswerFormat;
import ch.epfl.qedit.model.answer.MatrixFormat;
import ch.epfl.qedit.model.answer.MultiFieldFormat;
import ch.epfl.qedit.util.Callback;
import ch.epfl.qedit.util.Error;
import ch.epfl.qedit.util.Response;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** This class contains static methods useful to implements database interface */
public final class Util {

    private Util() {};

    // CONVERSION METHODS FROM FIRESTORE TO SPECIALIZE OBJECTS

    /**
     * Convert a document to a MatrixFormat
     *
     * @param doc a map containing the parameter useful for a initialize a MatrixFormat
     * @return a MatrixFormat or null if doc is not a valid description of a MatrixFormat
     */
    private static MatrixFormat matrixConvert(Map<String, Object> doc) {
        // TODO fill correctly this function in a later PR, MatrixFormat is currently changing
        return new MatrixFormat(1, 1);
    }

    /**
     * Convert a document to an AnswerFormat
     *
     * @param doc a map containing the parameter useful for a initialize an AnswerFormat
     * @return a AnswerFormat or null if doc is not a valid description of any AnswerFormat
     */
    private static AnswerFormat convertToAnswerFormat(Map<String, Object> doc) {
        if (doc.containsKey("matrix")) {
            return matrixConvert(doc);
        } else {
            return null;
        }
    }

    /**
     * Convert a document to an AnswerFormat, if there is more than one element in the docs list,
     * return a MultiFieldAnswerFormat
     *
     * @param docs a list of documents retrieve from firestore
     * @return a AnswerFormat or null if docs contains invalid description of AnswerFormat
     */
    public static AnswerFormat convertToAnswerFormat(List<Map<String, Object>> docs) {

        if (docs == null || docs.isEmpty()) {
            return null;
        } else if (docs.size()
                == 1) { // If there is only one document in the list, then we don't return a
            // multiField
            return convertToAnswerFormat(docs.get(0));
        }

        ArrayList<AnswerFormat> answers = new ArrayList<>();

        // Convert each document into a AnswerFormat
        for (Map<String, Object> doc : docs) {
            AnswerFormat answerFormat = convertToAnswerFormat(doc);

            // If a conversion has failed, the conversion of the entire list fails
            if (answerFormat == null) {
                return null;
            }
            answers.add(answerFormat);
        }

        return new MultiFieldFormat(answers);
    }

    /**
     * Convert a document snapshot from firestore to a Question object
     *
     * @param doc document retrieve from firestore database
     * @return a Question object, convert from doc
     */
    public static Question convertToQuestion(QueryDocumentSnapshot doc) {
        ArrayList<Map<String, Object>> answers = cast(doc.get("answers"));
        return new Question(
                doc.getString("title"), doc.getString("text"), convertToAnswerFormat(answers));
    }

    /**
     * Convert a query snapshot from firestore to a Quiz object
     *
     * @param docs list of documents retrieve from firestore database
     * @return a Quiz object, convert from docs
     */
    public static Quiz convertToQuiz(QuerySnapshot docs) {
        ArrayList<Question> questions = new ArrayList<>();
        for (QueryDocumentSnapshot doc : docs) {
            questions.add(convertToQuestion(doc));
        }
        return new Quiz("main_title", questions);
    }

    /**
     * Convert a document snapshot from firestore to a Map<String, String>
     *
     * @param doc document retrieve from firestore database
     * @return a string pool in a form of map Map<String, String>
     */
    public static Map<String, String> convertToStringPool(DocumentSnapshot doc) {
        HashMap<String, String> map = new HashMap<>();
        Map<String, Object> data = doc.getData();
        if (data == null) {
            return map;
        }
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (entry.getValue() instanceof String) {
                map.put(entry.getKey(), (String) entry.getValue());
            }
        }
        return map;
    }

    // SOME OTHER USEFUL METHODS TO IMPLEMENT THE BACKEND INTERFACE

    /**
     * Check if the condition pass as argument is true. If not, the response callback is triggered
     * with an error. This function is an helper function.
     *
     * @param condition boolean that need to be true, otherwise the responseCallback is triggered
     *     with an error
     * @param responseCallback Callback function triggered with an error if the condition is not
     *     respected
     * @param error Error with which we triggered the responseCallback if needed
     * @param <T> This function is generic because we don't really need to know the type of response
     * @return condition
     */
    public static <T> boolean require(
            boolean condition, Callback<Response<T>> responseCallback, Error error) {
        if (condition) {
            return true;
        } else {
            Response<T> response = Response.error(error);
            responseCallback.onReceive(response);
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T cast(Object object) {
        return (T) object;
    }
}
