package ch.epfl.qedit.backend.database;

import static ch.epfl.qedit.model.answer.MatrixFormat.Field.TO_MAP_TEXT;

import ch.epfl.qedit.model.Question;
import ch.epfl.qedit.model.StringPool;
import ch.epfl.qedit.model.answer.AnswerFormat;
import ch.epfl.qedit.model.answer.MatrixFormat;
import ch.epfl.qedit.model.answer.MatrixModel;
import ch.epfl.qedit.model.answer.MultiFieldFormat;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/** This class contains static methods useful to implements database interface */
public final class Util {

    // This class is only a namespace for helper functions, so it should not be instantiable
    private Util() {}

    /**
     * This type of exception indicates that a request couldn't be answered, either because of a
     * connection error, or a malformed request.
     */
    static class RequestException extends Exception {
        RequestException(String message) {
            super(message);
        }
    }

    /**
     * This type of exception indicates that the data from the database has the wrong format. In
     * practice, it should never happen, but can be used to debug the database.
     */
    public static class FormatException extends Exception {
        FormatException(String message) {
            super(message);
        }
    }

    /** This allows to complete the future with a request exception */
    public static void error(CompletableFuture<?> future, String message) {
        future.completeExceptionally(new RequestException(message));
    }

    /** Completes the given future with an exception */
    private static <T> void formatError(CompletableFuture<T> future, String error) {
        future.completeExceptionally(new FormatException(error));
    }

    /** Extracts the list of languages from a Firestore document */
    public static void extractLanguages(
            CompletableFuture<List<String>> future, DocumentSnapshot doc) {
        Object languages = doc.get("languages");

        if (languages == null)
            formatError(future, "The quiz should contain a list of supported languages");

        future.complete((List<String>) languages);
    }

    /** Extracts the quiz from a Firestore document */
    public static void extractQuestions(
            CompletableFuture<List<Question>> future, QuerySnapshot query) {
        List<Question> questions = new ArrayList<>();

        try {
            for (QueryDocumentSnapshot doc : query) questions.add(extractQuestion(doc));
        } catch (FormatException e) {
            future.completeExceptionally(e);
            return;
        }

        future.complete(questions);
    }

    /** Extract a question from a firestore document */
    private static Question extractQuestion(QueryDocumentSnapshot doc) throws FormatException {
        String title = doc.getString(Question.TO_MAP_TITLE);
        String text = doc.getString(Question.TO_MAP_TEXT);
        List<Object> answers = (List<Object>) doc.get(Question.TO_MAP_ANSWERS);

        if (title == null || text == null || answers == null)
            throw new FormatException("Invalid question: missing title, text or answers");

        AnswerFormat formats = extractAnswerFormats(answers);

        // Extract treasure hunt parameters
        Double radius = doc.getDouble(Question.TO_MAP_RADIUS);
        if (radius == null) return new Question(title, text, formats);

        Double latitude = doc.getDouble(Question.TO_MAP_LATITUDE);
        Double longitude = doc.getDouble(Question.TO_MAP_LONGITUDE);

        if (longitude == null || latitude == null)
            throw new FormatException("Invalid question: missing latitude or longitude");

        return new Question(title, text, formats, longitude, latitude, radius);
    }

    /** Extract an answer format if it is a MultiField */
    public static AnswerFormat extractAnswerFormats(List<Object> docs) throws FormatException {
        if (docs.isEmpty())
            throw new FormatException("Invalid question: it should contain at least one answer");
        else if (docs.size() == 1) return extractAnswerFormat((Map<String, Object>) docs.get(0));

        List<AnswerFormat> formats = new ArrayList<>();

        for (Object doc : docs) formats.add(extractAnswerFormat((Map<String, Object>) doc));

        return new MultiFieldFormat(formats);
    }

    /** Extract an answer format if it is a single answer format */
    public static AnswerFormat extractAnswerFormat(Map<String, Object> doc) throws FormatException {
        String type = (String) doc.get(AnswerFormat.TO_MAP_TYPE);

        if (type == null) throw new FormatException("Invalid answer format: missing type");
        else if (type.equals(MatrixFormat.TYPE)) return extractMatrixFormat(doc);
        else throw new FormatException("Invalid answer format");
    }

    public static MatrixFormat extractMatrixFormat(Map<String, Object> doc) throws FormatException {
        Integer rows = (Integer) doc.get(MatrixFormat.TO_MAP_NUM_ROWS);
        Integer columns = (Integer) doc.get(MatrixFormat.TO_MAP_NUM_COLUMNS);
        Map<String, Object> matrix = (Map<String, Object>) doc.get(MatrixFormat.TO_MAP_FIELDS);

        if (rows == null || columns == null || matrix == null)
            throw new FormatException("Invalid matrix format: missing rows, columns or matrix");

        MatrixFormat.Builder builder = new MatrixFormat.Builder(rows, columns);

        for (Map.Entry<String, Object> entry : matrix.entrySet()) {
            int[] index = extractFieldIndex(entry.getKey(), rows, columns);
            MatrixFormat.Field field = extractField((Map<String, Object>) entry.getValue());
            builder.withField(index[0], index[1], field);
        }
        MatrixFormat answer = builder.build();
        answer.setCorrectAnswer(extractMatrixSolution(doc));
        return answer;
    }

    public static MatrixModel extractMatrixSolution(Map<String, Object> doc)
            throws FormatException {
        Integer rows = (Integer) doc.get(MatrixModel.TO_MAP_NUM_ROWS);
        Integer columns = (Integer) doc.get(MatrixModel.TO_MAP_NUM_COLUMNS);
        Map<String, String> solution = (Map<String, String>) doc.get(MatrixModel.TO_MAP_DATA);

        if (rows == null || columns == null || solution == null)
            throw new FormatException("Invalid matrix format: missing solution");

        MatrixModel model = new MatrixModel(rows, columns);
        for (Map.Entry<String, String> entry : solution.entrySet()) {
            int[] index = extractFieldIndex(entry.getKey(), rows, columns);
            model.updateAnswer(index[0], index[1], entry.getValue());
        }

        return model;
    }

    /** A field in a matrix that is store in firestore as a document field name : "i,j" */
    public static int[] extractFieldIndex(String key, int rows, int cols) throws FormatException {
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

    public static MatrixFormat.Field extractField(Map<String, Object> field)
            throws FormatException {
        String typeString = (String) field.get(MatrixFormat.Field.TO_MAP_TYPE);
        String text = (String) field.get(TO_MAP_TEXT);

        if (typeString == null || text == null)
            throw new FormatException(
                    "Invalid field for matrix format: missing type, text or max_characters");

        try {
            MatrixFormat.Field.Type type = MatrixFormat.Field.Type.valueOf(typeString);
            return new MatrixFormat.Field(type, text);
        } catch (Exception e) {
            throw new FormatException("Unknown field type");
        }
    }

    /** Extracts the string pool from a Firestore document */
    public static void extractStringPool(
            CompletableFuture<StringPool> future, DocumentSnapshot doc, String language) {
        Map<String, Object> data = doc.getData();
        Map<String, String> result = new HashMap<>();

        for (Map.Entry<String, Object> entry : data.entrySet()) {
            Object value = entry.getValue();

            if (value instanceof String) {
                result.put(entry.getKey(), (String) value);
            } else {
                formatError(future, "A string pool should only contain string values");
                return;
            }
        }
        StringPool stringPool = new StringPool(result);
        stringPool.setLanguageCode(language);
        future.complete(stringPool);
    }

    public static void extractTreasureHunt(
            CompletableFuture<Boolean> future, DocumentSnapshot doc) {
        Boolean treasureHunt = doc.getBoolean("treasureHunt");
        if (treasureHunt == null)
            formatError(future, "A quiz must specify if it is a treasure hunt or not");
        else future.complete(treasureHunt);
    }

    /** Update data specified in data Map in Firestore User (user ID) */
    public static CompletableFuture<Void> updateUser(
            FirebaseFirestore db, String userId, Map<String, Object> data) {

        CompletableFuture<Void> future = new CompletableFuture<>();

        db.collection("users")
                .document(userId)
                .set(data, SetOptions.merge())
                .addOnSuccessListener(doc -> future.complete(null))
                .addOnFailureListener(e -> error(future, e.getMessage()));

        return future;
    }

    /** Helper function to upload a string pool of a quiz */
    static CompletableFuture<Void> uploadStringPool(
            FirebaseFirestore db, String quizId, StringPool stringPool) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        db.collection("quizzes")
                .document(quizId)
                .collection("stringPools")
                .document(stringPool.getLanguageCode())
                .set(stringPool.toMap(), SetOptions.merge())
                .addOnSuccessListener(ref -> future.complete(null))
                .addOnFailureListener(e -> error(future, e.getMessage()));

        return future;
    }

    static CompletableFuture<Void> uploadQuestions(
            FirebaseFirestore db, String quizId, List<Question> questions) {
        CollectionReference questionsRef =
                db.collection("quizzes").document(quizId).collection("questions");

        CompletableFuture[] futures = new CompletableFuture[questions.size()];
        // There is no efficient way in firestore to upload a full collection in one
        // operation
        for (int i = 0; i < questions.size(); i++) {
            // Upload a single question to firestore
            CompletableFuture<Void> future = new CompletableFuture<>();
            Map<String, Object> doc = questions.get(i).toMap();
            doc.put("index", i);
            questionsRef
                    .add(doc)
                    .addOnSuccessListener(ref -> future.complete(null))
                    .addOnFailureListener(e -> error(future, e.getMessage()));
            futures[i] = future;
        }
        // Combine all the futures of each question
        return CompletableFuture.allOf(futures);
    }
}
