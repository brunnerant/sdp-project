package ch.epfl.qedit.backend.database;

import android.content.Context;
import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.model.StringPool;
import ch.epfl.qedit.model.User;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
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

    // This it the cache directory
    private final File userCacheDir;

    /** Creates a cache that decorates the given database service */
    public CacheDBService(DatabaseService dbService, Context context) {
        this.dbService = dbService;
        this.userCacheDir = new File(context.getCacheDir(), "users");

        // Create an empty directory if it does not already exist
        if (!userCacheDir.exists()) userCacheDir.mkdirs();
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
        CompletableFuture<User> future = new CompletableFuture<>();

        File userFile = new File(userCacheDir, userId);
        User fromCache = lookup(userFile);

        if (fromCache == null) {
            // If the user was not in the cache, we retrieve it from the real database
            dbService
                    .getUser(userId)
                    .thenAccept(
                            user -> {
                                // We store the user in the files for the next time
                                store(userFile, user);
                                future.complete(user);
                            });
        } else {
            // If the user was in the cache, no need to access the database
            future.complete(fromCache);
        }

        return future;
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

    /** Lookups an item in the file storage if it exists. Returns null if not present. */
    private <T extends Serializable> T lookup(File file) {
        // If the file does not exist, the item was not cached
        if (!file.exists()) return null;

        // Otherwise, try to read it from the file
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file))) {
            return (T) inputStream.readObject();
        } catch (Exception e) {
            return null;
        }
    }

    /** Stores an item in the file storage. */
    private <T extends Serializable> void store(File file, T data) {
        // Try to store the data in the cache
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file))) {
            outputStream.writeObject(data);
        } catch (Exception ignored) {
        }
    }
}
