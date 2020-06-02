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
import ch.epfl.qedit.backend.database.MockDBService;
import ch.epfl.qedit.model.User;
import java.io.File;
import java.util.concurrent.CompletableFuture;
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

    @Test
    public void testCacheCachesRequests() {
        CompletableFuture<User> f1 = cache.getUser(ANTHONY_IOZZIA_ID);
        CompletableFuture<User> f2 = cache.getUser(ANTHONY_IOZZIA_ID);

        User u1 = f1.join();
        User u2 = f2.join();

        verify(db, times(1)).getUser(ANTHONY_IOZZIA_ID);
        assertEquals(u1, u2);
    }
}
