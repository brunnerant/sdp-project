package ch.epfl.qedit.backend.database;

import ch.epfl.qedit.model.Question;
import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.model.StringPool;
import ch.epfl.qedit.model.answer.AnswerFormat;
import ch.epfl.qedit.model.answer.MatrixFormat;
import ch.epfl.qedit.model.answer.MultiFieldFormat;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/** This class contains static methods useful to implements database interface */
public final class Util {

    // This class is only a namespace for helper functions, so it should not be instantiable
    private Util() {};

    /**
     * This type of exception indicates that a request couldn't be answered, either because of a
     * connection error, or a malformed request.
     */
    static class RequestException extends Exception {
        public RequestException(String message) {
            super(message);
        }
    }

    /**
     * This type of exception indicates that the data from the database has the wrong format. In
     * practice, it should never happen, but can be used to debug the database.
     */
    static class FormatException extends Exception {
        public FormatException(String message) {
            super(message);
        }
    }

    /** Completes the given future with an exception */
    static <T> void error(CompletableFuture<T> future, String error) {
        future.completeExceptionally(new FormatException(error));
    }

    /** Extracts the list of languages from a Firestore document */
    static void extractLanguages(CompletableFuture<List<String>> future, DocumentSnapshot doc) {
        Object languages = doc.get("languages");

        if (languages == null)
            error(future, "The quiz should contain a list of supported languages");

        future.complete((List<String>) languages);
    }

    /** Extracts the quiz from a Firestore document */
    static void extractQuiz(CompletableFuture<Quiz> future, QuerySnapshot query) {
        List<Question> questions = new ArrayList<>();

        try {
            for (QueryDocumentSnapshot doc : query) questions.add(extractQuestion(doc));
        } catch (FormatException e) {
            future.completeExceptionally(e);
            return;
        }

        future.complete(new Quiz("main_title", questions));
    }

    private static Question extractQuestion(QueryDocumentSnapshot doc) throws FormatException {
        String title = doc.getString("title");
        String text = doc.getString("text");
        List<Object> answers = (List<Object>) doc.get("answers");

        if (title == null || text == null || answers == null)
            throw new FormatException("Invalid question: missing title, text or answers");

        return new Question(title, text, extractAnswerFormats(answers));
    }

    private static AnswerFormat extractAnswerFormats(List<Object> docs) throws FormatException {
        if (docs.isEmpty())
            throw new FormatException("Invalid question: it should contain at least one answer");
        else if (docs.size() == 1) return extractAnswerFormat((Map<String, Object>) docs.get(0));

        List<AnswerFormat> formats = new ArrayList<>();

        for (Object doc : docs) formats.add(extractAnswerFormat((Map<String, Object>) doc));

        return new MultiFieldFormat(formats);
    }

    private static AnswerFormat extractAnswerFormat(Map<String, Object> doc)
            throws FormatException {
        String type = (String) doc.get("type");

        if (type == null) throw new FormatException("Invalid answer format: missing type");
        else if (type.equals("matrix")) return extractMatrixFormat(doc);
        else throw new FormatException("Invalid answer format");
    }

    private static MatrixFormat extractMatrixFormat(Map<String, Object> doc)
            throws FormatException {
        Integer rows = (Integer) doc.get("rows");
        Integer columns = (Integer) doc.get("columns");
        Map<String, Object> matrix = (Map<String, Object>) doc.get("matrix");

        if (rows == null || columns == null || matrix == null)
            throw new FormatException("Invalid matrix format: missing rows, columns or matrix");

        MatrixFormat.Builder builder = new MatrixFormat.Builder(rows, columns);

        for (Map.Entry<String, Object> entry : matrix.entrySet()) {
            int[] index = extractIndex(entry.getKey(), rows, columns);
            MatrixFormat.Field field = extractField((Map<String, Object>) entry.getValue());
            builder.withField(index[0], index[1], field);
        }

        return builder.build();
    }

    private static int[] extractIndex(String key, int rows, int cols) throws FormatException {
        String[] parts = key.split(",");

        if (parts.length != 2) throw new FormatException("Illegal field index format");

        int row, col;
        try {
            row = Integer.parseInt(parts[0]);
            col = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            throw new FormatException("Illegal field index format: the indices should be integers");
        }

        if (row < 0 || row >= rows || col < 0 || col >= cols)
            throw new FormatException("Illegal field index format: index out of bound");

        return new int[] {row, col};
    }

    private static MatrixFormat.Field extractField(Map<String, Object> field)
            throws FormatException {
        String typeString = (String) field.get("type");
        String text = (String) field.get("text");
        Integer maxCharacters = (Integer) field.get("mac_characters");

        if (typeString == null || text == null || maxCharacters == null)
            throw new FormatException(
                    "Invalid field for matrix format: missing type, text or max_characters");

        return new MatrixFormat.Field(extractFieldType(typeString), maxCharacters, text);
    }

    private static MatrixFormat.Field.Type extractFieldType(String type) throws FormatException {
        switch (type) {
            case "pre_filled":
                return MatrixFormat.Field.Type.PreFilled;
            case "text":
                return MatrixFormat.Field.Type.Text;
            case "unsigned_int":
                return MatrixFormat.Field.Type.UnsignedInt;
            case "signed_int":
                return MatrixFormat.Field.Type.SignedInt;
            case "unsigned_float":
                return MatrixFormat.Field.Type.UnsignedFloat;
            case "signed_float":
                return MatrixFormat.Field.Type.SignedFloat;
            default:
                throw new FormatException("Unknown field type");
        }
    }

    /** Extracts the string pool from a Firestore document */
    static void extractStringPool(CompletableFuture<StringPool> future, DocumentSnapshot doc) {
        Map<String, Object> data = doc.getData();
        Map<String, String> result = new HashMap<>();

        for (Map.Entry<String, Object> entry : data.entrySet()) {
            Object value = entry.getValue();

            if (value instanceof String) {
                result.put(entry.getKey(), (String) value);
            } else {
                error(future, "A string pool should only contain string values");
                return;
            }
        }

        future.complete(new StringPool(result));
    }
}
