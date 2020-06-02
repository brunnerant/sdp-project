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
import java.util.function.Supplier;

/**
 * This class decorates a database service so that some of the requests are cached on the internal
 * storage. This is used to reduce the delay of accessing the database and allowing the user to use
 * the app without internet connection. An important assumption that is made is that the data that
 * is in the database will not be updated. If this assumption is not valid, the cache might contain
 * stale data.
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
        return retrieve(new File(userCacheDir, userId), () -> dbService.getUser(userId));
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

    /**
     * Lookups an item in the file storage if it exists. Returns a completable future that returns
     * the result once available, or null if the data was not found in the cache.
     */
    private <T extends Serializable> CompletableFuture<T> lookup(File file) {
        CompletableFuture<T> future = new CompletableFuture<>();

        new Thread(
                        () -> {
                            // If the file does not exist, the item was not cached
                            if (!file.exists()) future.complete(null);

                            // Otherwise, try to read it from the file
                            try (ObjectInputStream inputStream =
                                    new ObjectInputStream(new FileInputStream(file))) {
                                future.complete((T) inputStream.readObject());
                            } catch (Exception e) {
                                // We don't return exceptionally here, because we might still be
                                // able to
                                // find the data in the real database.
                                future.complete(null);
                            }
                        })
                .run();

        return future;
    }

    /**
     * Stores an item in the file storage. It does so in a separate thread so that the rest of the
     * logic can already be executed.
     */
    private <T extends Serializable> void storeInCache(File file, T data) {
        new Thread(
                        () -> {
                            // Try to store the data in the cache (append = false, so that it is
                            // overwritten)
                            try (ObjectOutputStream outputStream =
                                    new ObjectOutputStream(new FileOutputStream(file, false))) {
                                outputStream.writeObject(data);
                            } catch (Exception ignored) {
                            }
                        })
                .run();
    }

    /**
     * Performs a retrieval from the cache or the database, depending on their availability. The
     * data is first searched in the cache. If it is not present there, it is fetched from the
     * database and stored in the cache for the next retrievals.
     */
    private <T extends Serializable> CompletableFuture<T> retrieve(
            File cache, Supplier<CompletableFuture<T>> database) {
        return lookup(cache)
                .thenCompose(
                        fromCache -> {
                            if (fromCache != null) {
                                // If the data is already available, we can return it immediately
                                return CompletableFuture.<T>completedFuture(fromCache);
                            } else {
                                // Otherwise, we fetch it from the real database
                                CompletableFuture<T> f = database.get();

                                // Once it arrives, we cache it locally
                                f.thenAccept(
                                        fromDB -> {
                                            storeInCache(cache, fromDB);
                                        });

                                return f;
                            }
                        });
    }
}
