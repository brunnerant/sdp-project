package ch.epfl.qedit.Search;

import org.junit.Test;

import java.util.concurrent.ExecutionException;

import ch.epfl.qedit.backend.database.MockDBService;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class searchTest {
    private MockDBService db = new MockDBService();

    @Test
    public void search() throws ExecutionException, InterruptedException {
        assertFalse(db.searchDatabase(2, 10, "Title").get().isEmpty());
        assertTrue(db.searchDatabase(2,  10, "qsdvsuiz").get().isEmpty());
    }


}
