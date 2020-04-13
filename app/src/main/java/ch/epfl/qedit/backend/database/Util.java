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

public class Util {

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

    // ANSWER //

    private static MatrixFormat matrixConvert(Map<String, Object> doc) {
        // TODO fill this function
        return null;
    }

    private static AnswerFormat convertToAnswerFormat(Map<String, Object> doc) {
        if (doc.containsKey("matrix")) {
            return matrixConvert(doc);
        } else {
            return null;
        }
    }

    private static AnswerFormat convertToAnswerFormat(List<Map<String, Object>> docs) {
        if (docs == null || docs.isEmpty()) {
            return null;
        } else if (docs.size() == 1) {
            return convertToAnswerFormat(docs.get(0));
        }
        ArrayList<AnswerFormat> answers = new ArrayList<>();
        for (Map<String, Object> doc : docs) {
            answers.add(convertToAnswerFormat(doc));
        }

        return new MultiFieldFormat(answers);
    }

    // CAST // TODO check if that s a good idea

    @SuppressWarnings("unchecked")
    public static <T> T cast(Object object) {
        return (T) object;
    }

    // QUESTION //

    public static Question convertToQuestion(QueryDocumentSnapshot doc) {
        ArrayList<Map<String, Object>> answers = cast(doc.get("answers"));
        return new Question(
                doc.getString("title"), doc.getString("text"), convertToAnswerFormat(answers));
    }

    // QUIZ //

    public static Quiz convertToQuiz(QuerySnapshot docs) {
        ArrayList<Question> questions = new ArrayList<>();
        for (QueryDocumentSnapshot doc : docs) {
            questions.add(convertToQuestion(doc));
        }
        return new Quiz("main_title", questions);
    }

    // STRING POOL //

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
}
