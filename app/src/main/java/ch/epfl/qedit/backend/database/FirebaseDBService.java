package ch.epfl.qedit.backend.database;

import static ch.epfl.qedit.backend.database.Util.updateUser;

import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.model.StringPool;
import ch.epfl.qedit.model.User;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

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
    public CompletableFuture<User> getUser(String userId) {
        CompletableFuture<User> future = new CompletableFuture<>();

        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(doc -> future.complete(doc.toObject(User.class)))
                .addOnFailureListener(e -> error(future, "The required user does not exist"));

        return future;
    }

    @Override
    public CompletableFuture<Void> createUser(String userId, String firstName, String lastName) {
        Map<String, Object> data = new HashMap<>();
        data.put("first_name", firstName);
        data.put("last_name", lastName);
        data.put("score", 0);
        data.put("successes", 0);
        data.put("attempts", 0);
        data.put("quizzes", new HashMap<>());

        // in firestore API it is specified that if there is no user with id userId, one will be
        // created
        return updateUser(db, userId, data);
    }

    @Override
    public CompletableFuture<Void> updateUserStatistics(
            String userId, int score, int successes, int attempts) {
        Map<String, Object> data = new HashMap<>();
        data.put("score", score);
        data.put("successes", successes);
        data.put("attempts", attempts);

        return updateUser(db, userId, data);
    }

    @Override
    public CompletableFuture<Void> updateUserQuizList(String userId, Map<String, String> quizzes) {
        Map<String, Object> data = new HashMap<>();
        data.put("quizzes", quizzes);

        return updateUser(db, userId, data);
    }
}
