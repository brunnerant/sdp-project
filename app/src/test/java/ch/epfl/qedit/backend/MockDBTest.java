package ch.epfl.qedit.backend;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import ch.epfl.qedit.backend.database.DatabaseService;
import ch.epfl.qedit.backend.database.MockDBService;
import ch.epfl.qedit.model.Question;
import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.util.Callback;
import ch.epfl.qedit.util.Error;
import ch.epfl.qedit.util.Response;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertNull;

public class MockDBTest {

    private MockDBService db = new MockDBService();
    private CountDownLatch lock = new CountDownLatch(1);
    private Error error;

    // TODO DELETE IN LATER PR =============================
    private String title;
    private Quiz quiz;
    private List<Question> questions;

    private Question bananaQuestion_fr =
            new Question("Banane", "Combien y a-t-il de bananes ?", "matrix1x1");
    private Question bananaQuestion_en =
            new Question("Banana", "How many bananas can you count?", "matrix1x1");
    // =====================================================

    private List<String> languages;
    private Map<String, String> stringPool;
    private Quiz struct;

    private HashMap<String, String> stringPool_en =
            new HashMap<String, String>() {
                {
                    put("main_title", "Title");
                    put("q1_title", "Banana");
                    put("q1_text", "How many bananas are there on Earth?");
                }
            };

    private HashMap<String, String> stringPool_fr =
            new HashMap<String, String>() {
                {
                    put("main_title", "Titre");
                    put("q1_title", "Banane");
                    put("q1_text", "Combien y a-t-il de bananes sur Terre ?");
                }
            };

    private Question question = new Question("q1_title", "q1_text", "matrix1x1");

    private void lockWait() {
        try {
            lock.await(3000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetQuizStringPool_en() {
        db.getQuizStringPool(
                "quiz1",
                "en",
                new Callback<Response<Map<String, String>>>() {
                    @Override
                    public void onReceive(Response<Map<String, String>> data) {
                        stringPool = data.getData();
                        error = data.getError();
                    }
                });
        lockWait();
        assertEquals(Response.NO_ERROR, error);
        assertEquals(stringPool_en, stringPool);
    }

    @Test
    public void testGetQuizStringPool_fr() {
        db.getQuizStringPool(
                "quiz1",
                "fr",
                new Callback<Response<Map<String, String>>>() {
                    @Override
                    public void onReceive(Response<Map<String, String>> data) {
                        stringPool = data.getData();
                        error = data.getError();
                    }
                });
        lockWait();
        assertEquals(Response.NO_ERROR, error);
        assertEquals(stringPool_fr, stringPool);
    }

    @Test
    public void testGetQuizStringPoolError() {
        db.getQuizStringPool(
                "error",
                "en",
                new Callback<Response<Map<String, String>>>() {
                    @Override
                    public void onReceive(Response<Map<String, String>> data) {
                        stringPool = data.getData();
                        error = data.getError();
                    }
                });
        lockWait();
        assertEquals(DatabaseService.WRONG_DOCUMENT, error);
        assertNull(stringPool);
    }

    @Test
    public void testQuizLanguagesError() {
        db.getQuizLanguages(
                "error",
                new Callback<Response<List<String>>>() {
                    @Override
                    public void onReceive(Response<List<String>> data) {
                        languages = data.getData();
                        error = data.getError();
                    }
                });
        lockWait();
        assertEquals(DatabaseService.WRONG_DOCUMENT, error);
        assertNull(languages);
    }

    @Test
    public void testQuizLanguages() {
        db.getQuizLanguages(
                "quiz1",
                new Callback<Response<List<String>>>() {
                    @Override
                    public void onReceive(Response<List<String>> data) {
                        languages = data.getData();
                        error = data.getError();
                    }
                });
        lockWait();
        assertEquals(Response.NO_ERROR, error);
        assertThat(languages, containsInAnyOrder("en", "fr"));
    }

    @Test
    public void testGetQuizStructError() {
        db.getQuizStructure(
                "error",
                new Callback<Response<Quiz>>() {
                    @Override
                    public void onReceive(Response<Quiz> data) {
                        struct = data.getData();
                        error = data.getError();
                    }
                });
        lockWait();
        assertEquals(DatabaseService.WRONG_DOCUMENT, error);
        assertNull(struct);
    }

    @Test
    public void testGetQuizStruct() {
        db.getQuizStructure(
                "quiz1",
                new Callback<Response<Quiz>>() {
                    @Override
                    public void onReceive(Response<Quiz> data) {
                        struct = data.getData();
                        error = data.getError();
                    }
                });
        lockWait();
        assertEquals(Response.NO_ERROR, error);
        assertEquals("main_title", struct.getTitle());
        assertEquals(Arrays.asList(question), struct.getQuestions());
    }

    // TODO DELETE IN LATER PR =============================

    @Test
    public void testGetTitle() {

        db.getQuizTitle(
                "quiz0",
                new Callback<Response<String>>() {
                    @Override
                    public void onReceive(Response<String> data) {
                        title = data.getData();
                        error = data.getError();
                    }
                });
        lockWait();
        assertEquals(Response.NO_ERROR, error);
        if (Locale.getDefault().getLanguage().equals("en")) {
            assertEquals("I am a Mock Quiz!", title);
        } else {
            assertEquals("Je suis un Mock Quiz !", title);
        }
    }

    @Test
    public void testGetQuestions() {

        db.getQuizQuestions(
                "quiz1",
                new Callback<Response<List<Question>>>() {
                    @Override
                    public void onReceive(Response<List<Question>> data) {
                        questions = data.getData();
                        error = data.getError();
                    }
                });
        lockWait();
        assertEquals(Response.NO_ERROR, error);
        if (Locale.getDefault().getLanguage().equals("en")) {
            assertTrue(questions.contains(bananaQuestion_en));
        } else {
            assertTrue(questions.contains(bananaQuestion_fr));
        }
    }

    @Test
    public void testGetQuiz() {

        db.getQuiz(
                "quiz1",
                new Callback<Response<Quiz>>() {
                    @Override
                    public void onReceive(Response<Quiz> data) {
                        quiz = data.getData();
                        error = data.getError();
                    }
                });

        lockWait();
        assertEquals(Response.NO_ERROR, error);
        if (Locale.getDefault().getLanguage().equals("en")) {
            assertTrue(quiz.getQuestions().contains(bananaQuestion_en));
            assertEquals("Title", quiz.getTitle());
        } else {
            assertTrue(quiz.getQuestions().contains(bananaQuestion_fr));
            assertEquals("Titre", quiz.getTitle());
        }
    }

    @Test
    public void testGetTitleFail() {

        db.getQuizTitle(
                "error",
                new Callback<Response<String>>() {
                    @Override
                    public void onReceive(Response<String> data) {
                        error = data.getError();
                    }
                });
        lockWait();
        assertEquals(DatabaseService.WRONG_DOCUMENT, error);
    }

    @Test
    public void testGetQuestionsFail() {

        db.getQuizQuestions(
                "error",
                new Callback<Response<List<Question>>>() {
                    @Override
                    public void onReceive(Response<List<Question>> data) {
                        error = data.getError();
                    }
                });
        lockWait();
        assertEquals(DatabaseService.WRONG_DOCUMENT, error);
    }

    @Test
    public void testGetQuizFail() {

        db.getQuiz(
                "error",
                new Callback<Response<Quiz>>() {
                    @Override
                    public void onReceive(Response<Quiz> data) {
                        error = data.getError();
                    }
                });
        lockWait();
        assertEquals(DatabaseService.WRONG_DOCUMENT, error);
    }

    // =====================================================
}
