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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * This class decorates a database service so that some of the requests are cached on the internal
 * storage. This is used to reduce the delay of accessing the database and allowing the user to use
 * the app without internet connection.
 */
public class CacheDBService implements DatabaseService {

    // This is the underlying database service that is being decorated
    private final DatabaseService dbService;

    // These are the directories in which we cache the data
    private final File userCacheDir;
    private final File quizCacheDir;
    private final File poolCacheDir;
    private final File langCacheDir;

    /** Creates a cache that decorates the given database service */
    public CacheDBService(DatabaseService dbService, Context context) {
        this.dbService = dbService;
        this.userCacheDir = new File(context.getCacheDir(), "users");
        this.quizCacheDir = new File(context.getCacheDir(), "quizzes");
        this.poolCacheDir = new File(context.getCacheDir(), "pools");
        this.langCacheDir = new File(context.getCacheDir(), "languages");

        // Create an empty directory if it does not already exist
        if (!userCacheDir.exists()) userCacheDir.mkdirs();
    }

    @Override
    public CompletableFuture<List<String>> getQuizLanguages(String quizId) {
        // Lists are not serializable, but array lists are, so we just need to wrap the lists
        // into array lists before storing them in the cache. Since array lists are lists,
        // nothing needs to be done to deserialize.
        return this.<List<String>, ArrayList<String>>retrieve(
                new File(langCacheDir, quizId),
                () -> dbService.getQuizLanguages(quizId),
                list -> new ArrayList<>(list),
                list -> list // cannot use Function.identity() because of the type system
                );
    }

    @Override
    public CompletableFuture<Quiz> getQuizStructure(String quizId) {
        return retrieve(new File(quizCacheDir, quizId), () -> dbService.getQuizStructure(quizId));
    }

    @Override
    public CompletableFuture<StringPool> getQuizStringPool(String quizId, String language) {
        return retrieve(
                new File(quizCacheDir, quizId + "-" + language),
                () -> dbService.getQuizStringPool(quizId, language));
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
        // For now, we don't implement a complex caching policy at all. Upon a write, we
        // simply invalidate the local cache.
        // A better approach would be to update the data locally and write it back later, for
        // example when the application exits. The problem with that approach is that it is
        // harder to avoid stale data, so we stick to the simple solution for now.

        // Invalidate the local cache
        File userFile = new File(userCacheDir, userId);
        userFile.delete();

        // And update the database
        return dbService.updateUserStatistics(userId, score, successes, attempts);
    }

    @Override
    public CompletableFuture<Void> updateUserQuizList(String userId, Map<String, String> quizzes) {
        // See the comment in the method updateUserStatistics to understand the choices
        // that were made in this functions.

        // Invalidate the local cache
        File userFile = new File(userCacheDir, userId);
        userFile.delete();

        // And update the database
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
    private <T extends Serializable> CompletableFuture<Void> storeInCache(File file, T data) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        new Thread(
                        () -> {
                            // Try to store the data in the cache (append = false, so that it is
                            // overwritten)
                            try (ObjectOutputStream outputStream =
                                    new ObjectOutputStream(new FileOutputStream(file, false))) {
                                outputStream.writeObject(data);
                            } catch (Throwable t) {
                                future.completeExceptionally(t);
                            }

                            future.complete(null);
                        })
                .run();

        return future;
    }

    /**
     * * Performs a retrieval from the cache or the database, depending on their availability. The
     * data is first searched in the cache. If it is not present there, it is fetched from the
     * database and stored in the cache for the next retrievals.
     *
     * @param cache the file in which the data should be stored
     * @param database a function that requests the data from the database, if needed
     * @param serialize a function that serializes the data before file storage
     * @param deserialize a function that deserializes the data when being loaded from the cache
     * @param <S> the type of the data
     * @param <T> the type of the data, once serialized to the cache
     * @return a future that complete once the full retrieval was performed
     */
    private <S, T extends Serializable> CompletableFuture<S> retrieve(
            File cache,
            Supplier<CompletableFuture<S>> database,
            Function<S, T> serialize,
            Function<T, S> deserialize) {
        return lookup(cache)
                .thenCompose(
                        fromCache -> {
                            if (fromCache != null) {
                                // If the data is already available, we can return it immediately
                                return CompletableFuture.<S>completedFuture(
                                        deserialize.apply((T) fromCache));
                            } else {
                                // Otherwise, we fetch it from the real database
                                CompletableFuture<S> f = database.get();

                                // Once it arrives, we cache it locally
                                f.thenAccept(
                                        fromDB -> {
                                            // We don't care if the data was successfully written
                                            // or not because we can re-fetch it later in case
                                            // of a failure.
                                            storeInCache(cache, serialize.apply(fromDB));
                                        });

                                return f;
                            }
                        });
    }

    /** Same version as above, when the data type is already serializable */
    private <T extends Serializable> CompletableFuture<T> retrieve(
            File cache, Supplier<CompletableFuture<T>> database) {
        return retrieve(cache, database, Function.identity(), Function.identity());
    }
}
