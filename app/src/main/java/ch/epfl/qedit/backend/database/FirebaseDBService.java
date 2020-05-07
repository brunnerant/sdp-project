package ch.epfl.qedit.backend.database;

import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.model.StringPool;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * This class is a database service that connects to the real Firestore backend. To have more
 * information on how we chose to represent our data, you can visit
 * https://github.com/brunnerant/sdp-project/wiki/Firestore-database-structure.
 */
public class FirebaseDBService implements DatabaseService {

    private FirebaseFirestore db;

    public FirebaseDBService() {
        // Access a Cloud Firestore instance
        db = FirebaseFirestore.getInstance();
    }

    /** Returns a document reference to the given quiz */
    private DocumentReference getQuizRef(String quizID) {
        return db.collection("quizzes").document(quizID);
    }

    /** This allows to complete the future with a request exception */
    private static void error(CompletableFuture<?> future, String message) {
        future.completeExceptionally(new Util.RequestException(message));
    }

    @Override
    public CompletableFuture<List<String>> getQuizLanguages(String quizId) {
        CompletableFuture<List<String>> future = new CompletableFuture<>();

        getQuizRef(quizId)
                .get()
                .addOnSuccessListener(doc -> Util.extractLanguages(future, doc))
                .addOnFailureListener(e -> error(future, "The required document does not exist"));

        return future;
    }

    @Override
    public CompletableFuture<Quiz> getQuizStructure(String quizId) {
        CompletableFuture<Quiz> future = new CompletableFuture<>();

        getQuizRef(quizId)
                .collection("questions")
                .orderBy("index")
                .get()
                .addOnSuccessListener(query -> Util.extractQuiz(future, query))
                .addOnFailureListener(e -> error(future, "The required document does not exist"));

        return future;
    }

    @Override
    public CompletableFuture<StringPool> getQuizStringPool(String quizId, String language) {
        CompletableFuture<StringPool> future = new CompletableFuture<>();

        getQuizRef(quizId)
                .collection("string_pools")
                .document(language)
                .get()
                .addOnSuccessListener(doc -> Util.extractStringPool(future, doc))
                .addOnFailureListener(e -> error(future, "The required document does not exist"));

        return future;
    }

    @Override
    public CompletableFuture<List<Map.Entry<String, String>>> searchDatabase(
            int start, int end, String search) {
        CompletableFuture<List<Map.Entry<String, String>>> future = new CompletableFuture<>();
        List<Map.Entry<String, String>> list = new ArrayList<>();

        for (DocumentSnapshot document :
                db.collection("quizzes").get().getResult().getDocuments()) {
            for (String s : document.getData().keySet()) {
                if (s.contains(search)) {
                    try {
                        list.add(
                                new AbstractMap.SimpleEntry<String, String>(
                                        s, getQuizStructure(s).get().getTitle()));
                    } catch (ExecutionException e) {
                        future.completeExceptionally(e);
                    } catch (InterruptedException e) {
                        future.completeExceptionally(e);
                    }
                }
            }
        }

        future.complete(list);
        return future;
    }
}
