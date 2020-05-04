package ch.epfl.qedit.backend;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;

import ch.epfl.qedit.backend.database.MockDBService;
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
    }

    @Test
    public void testCanAccessExistingData()
            throws InterruptedException, ExecutionException, TimeoutException {
        assertNotNull(db.getQuizLanguages("quiz0").get(3, TimeUnit.SECONDS));
        assertNotNull(db.getQuizStructure("quiz0").get(3, TimeUnit.SECONDS));
        assertNotNull(db.getQuizStringPool("quiz0", "en").get(3, TimeUnit.SECONDS));
    }
}
