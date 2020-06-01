package ch.epfl.qedit.backend.database;

import android.content.Context;
import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.model.StringPool;
import ch.epfl.qedit.model.User;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * This class decorates a database service so that some of the requests are cached on the internal
 * storage. This is used to reduce the delay of accessing the database and allowing the user to use
 * the app without internet connection.
 */
public class CacheDBService implements DatabaseService {

    // This is the underlying database service that is being decorated
    private final DatabaseService dbService;

    // This is the context of the application, that we need in order to access the cache
    private final Context context;

    /** Creates a cache that decorates the given database service */
    public CacheDBService(DatabaseService dbService, Context context) {
        this.dbService = dbService;
        this.context = context;
    }

    @Override
    public CompletableFuture<List<String>> getQuizLanguages(String quizId) {
        return dbService.getQuizLanguages(quizId);
    }

    @Override
    public CompletableFuture<Quiz> getQuizStructure(String quizId) {
        return dbService.getQuizStructure(quizId);
    }

    @Override
    public CompletableFuture<StringPool> getQuizStringPool(String quizId, String language) {
        return dbService.getQuizStringPool(quizId, language);
    }

    @Override
    public CompletableFuture<String> uploadQuiz(Quiz quiz, StringPool stringPool) {
        return dbService.uploadQuiz(quiz, stringPool);
    }

    @Override
    public CompletableFuture<User> getUser(String userId) {
        return dbService.getUser(userId);
    }

    @Override
    public CompletableFuture<Void> createUser(String userId, String firstName, String lastName) {
        return dbService.createUser(userId, firstName, lastName);
    }

    @Override
    public CompletableFuture<Void> updateUserStatistics(
            String userId, int score, int successes, int attempts) {
        return dbService.updateUserStatistics(userId, score, successes, attempts);
    }

    @Override
    public CompletableFuture<Void> updateUserQuizList(String userId, Map<String, String> quizzes) {
        return dbService.updateUserQuizList(userId, quizzes);
    }

    private static User getUserFromCache(String userId) {
        return null;
    }
}
