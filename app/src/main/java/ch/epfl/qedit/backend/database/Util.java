package ch.epfl.qedit.backend;

import ch.epfl.qedit.model.Question;
import ch.epfl.qedit.model.StringPool;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
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
    public static class RequestException extends Exception {
        public RequestException(String message) {
            super(message);
        }
    }

    /** This allows to complete the future with a request exception */
    public static void error(CompletableFuture<?> future, String message) {
        future.completeExceptionally(new RequestException(message));
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

    public static CompletableFuture<Void> uploadStringPool(
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

    public static CompletableFuture<Void> uploadQuestions(
            FirebaseFirestore db, String quizId, List<Question> questions) {
        CollectionReference questionsRef =
                db.collection("quizzes").document(quizId).collection("questions");

        CompletableFuture[] futures = new CompletableFuture[questions.size()];
        // There is no efficient way in firestore to upload a full collection in one
        // operation
        for (int i = 0; i < questions.size(); i++) {
            CompletableFuture<Void> future = new CompletableFuture<>();
            Map<String, Object> doc = questions.get(i).toMap();
            doc.put("index", i);
            questionsRef
                    .add(doc)
                    .addOnSuccessListener(ref -> future.complete(null))
                    .addOnFailureListener(e -> error(future, e.getMessage()));
            futures[i] = future;
        }

        return CompletableFuture.allOf(futures);
    }
}
