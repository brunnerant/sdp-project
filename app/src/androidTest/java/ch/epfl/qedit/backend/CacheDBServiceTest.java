package ch.epfl.qedit.backend;

import static ch.epfl.qedit.backend.auth.MockAuthService.ANTHONY_IOZZIA_ID;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import android.content.Context;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import ch.epfl.qedit.backend.database.CacheDBService;
import ch.epfl.qedit.backend.database.DatabaseService;
import ch.epfl.qedit.backend.database.MockDBService;
import java.io.File;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4ClassRunner.class)
public class CacheDBServiceTest {
    private Context context = ApplicationProvider.getApplicationContext();
    private File cacheDir = context.getCacheDir();

    private MockDBService db;
    private CacheDBService cache;

    // Recursively deletes a file or a directory
    private void delete(File fileOrDir) {
        if (fileOrDir.isDirectory()) {
            for (File child : fileOrDir.listFiles()) delete(child);
        }

        fileOrDir.delete();
    }

    // Clears all the files that might have been cached in the phone's internal storage
    private void clearCache() {
        delete(new File(cacheDir, "users"));
        delete(new File(cacheDir, "quizzes"));
        delete(new File(cacheDir, "pools"));
        delete(new File(cacheDir, "languages"));
    }

    @Before
    public void init() {
        clearCache();

        // We instantiate the DB service using Mockito's spy feature to monitor the calls
        db = spy(new MockDBService());
        cache = new CacheDBService(db, context);
    }

    @After
    public void cleanup() {
        clearCache();
    }

    // Tests that doing two subsequent requests to the same data calls the database only once
    private <T> void assertCached(Function<DatabaseService, CompletableFuture<T>> request) {
        CompletableFuture<T> future1 = request.apply(cache);
        CompletableFuture<T> future2 = request.apply(cache);

        T result1 = future1.join();
        T result2 = future2.join();

        // Only the first request should have gone to the database, the second one being cached
        request.apply(verify(db, times(1)));

        // The cached result should be the same as the result from the database
        assertEquals(result1, result2);
    }

    @Test
    public void testCacheCachesRequests() {
        assertCached(db -> db.getUser(ANTHONY_IOZZIA_ID));
        assertCached(db -> db.getQuizLanguages("quiz0"));
        assertCached(db -> db.getQuizStructure("quiz0"));
        assertCached(db -> db.getQuizStringPool("quiz0", "en"));
    }

    // Checks that the action invalidates the cache for a user
    private void assertInvalidatesUser(Runnable action) {
        cache.getUser(ANTHONY_IOZZIA_ID).join();
        action.run();
        cache.getUser(ANTHONY_IOZZIA_ID).join();

        // The database should be called twice because of invalidation
        verify(db, times(2)).getUser(ANTHONY_IOZZIA_ID);
    }

    @Test
    public void testUpdateStatisticsInvalidatesCache() {
        assertInvalidatesUser(() -> cache.updateUserStatistics(ANTHONY_IOZZIA_ID, 0, 0, 0));
    }

    @Test
    public void testUpdateQuizListInvalidatesCache() {
        assertInvalidatesUser(
                () -> cache.updateUserQuizList(ANTHONY_IOZZIA_ID, Collections.emptyMap()));
    }
}
