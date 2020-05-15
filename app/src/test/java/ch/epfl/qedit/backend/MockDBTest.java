package ch.epfl.qedit.backend;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;

import ch.epfl.qedit.backend.auth.MockAuthService;
import ch.epfl.qedit.backend.database.MockDBService;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;
import org.junit.Test;

public class MockDBTest {

    private MockDBService db = new MockDBService();

    private void assertFutureThrows(Supplier<CompletableFuture<?>> sup) {
        assertThrows(ExecutionException.class, () -> sup.get().get(3, TimeUnit.SECONDS));
    }

    @Test
    public void testCannotAccessNonExistentData() {
        assertFutureThrows(() -> db.getQuizStructure("unknown"));
        assertFutureThrows(() -> db.getQuizLanguages("unknown"));
        assertFutureThrows(() -> db.getQuizStringPool("unknown", "en"));
        assertFutureThrows(() -> db.getQuizStringPool("quiz0", "it"));
        assertFutureThrows(() -> db.getUser("unknown"));
        assertFutureThrows(() -> db.updateUserQuizList("unknown", null));
        assertFutureThrows(() -> db.updateUserStatistics("unknown", 0, 0, 0));
    }

    @Test
    public void testCanAccessExistingData()
            throws InterruptedException, ExecutionException, TimeoutException {
        assertNotNull(db.getQuizLanguages("quiz0").get(3, TimeUnit.SECONDS));
        assertNotNull(db.getQuizStructure("quiz0").get(3, TimeUnit.SECONDS));
        assertNotNull(db.getQuizStringPool("quiz0", "en").get(3, TimeUnit.SECONDS));
        assertNotNull(db.getUser(MockAuthService.ANTHONY_IOZZIA_ID).get(3, TimeUnit.SECONDS));

        //noinspection SpellCheckingInspection
        assertNull(
                db.createUser("T78XRGU3EMkgm5YED52Q", "JoJo", "Johnson").get(3, TimeUnit.SECONDS));
        assertNull(
                db.updateUserQuizList(MockAuthService.ANTHONY_IOZZIA_ID, new HashMap<>())
                        .get(3, TimeUnit.SECONDS));
        assertNull(
                db.updateUserStatistics(MockAuthService.ANTHONY_IOZZIA_ID, 0, 0, 0)
                        .get(3, TimeUnit.SECONDS));
    }
}
