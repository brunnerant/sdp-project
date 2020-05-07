package ch.epfl.qedit.Search;

import org.junit.Test;

import java.util.concurrent.ExecutionException;

import ch.epfl.qedit.backend.database.MockDBService;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

public class searchTest {
    private MockDBService db = new MockDBService();

    @Test
    public void search() throws ExecutionException, InterruptedException {
        assertFalse(db.searchDatabase(2, "quiz").get().isEmpty());
        assertTrue(db.searchDatabase(2, "qsdvsuiz").get().isEmpty());
    }


}
