package ch.epfl.qedit.backend;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;

import ch.epfl.qedit.backend.auth.MockAuthService;
import ch.epfl.qedit.backend.database.MockDBService;
import ch.epfl.qedit.model.Question;
import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.model.StringPool;
import ch.epfl.qedit.model.answer.MatrixFormat;
import java.util.Arrays;
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

    @Test
    public void testUploadQuiz() throws InterruptedException, ExecutionException, TimeoutException {
        StringPool stringPool = new StringPool();
        stringPool.setLanguageCode("en");
        stringPool.update(StringPool.TITLE_ID, "A Quiz Test");
        MatrixFormat matrix = MatrixFormat.singleField(MatrixFormat.Field.textField("???"));
        Question question =
                new Question(
                        stringPool.add("Question title"),
                        stringPool.add("question title?"),
                        matrix);
        Quiz quiz = new Quiz(StringPool.TITLE_ID, Arrays.asList(question));
        String id = db.uploadQuiz(quiz, stringPool).get(3, TimeUnit.SECONDS);
        assertNotNull(id);
        StringPool stringPoolLoaded = db.getQuizStringPool(id, "en").get(3, TimeUnit.SECONDS);
        assertEquals(stringPool, stringPoolLoaded);
        Quiz quizLoaded = db.getQuizStructure(id).get(3, TimeUnit.SECONDS);
        assertEquals(quiz, quizLoaded);
    }
}
