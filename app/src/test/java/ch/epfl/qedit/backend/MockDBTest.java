package ch.epfl.qedit.backend;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

import ch.epfl.qedit.backend.database.DatabaseService;
import ch.epfl.qedit.backend.database.MockDBService;
import ch.epfl.qedit.model.Question;
import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.util.Callback;
import ch.epfl.qedit.util.Error;
import ch.epfl.qedit.util.Response;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Test;

public class MockDBTest {
    MockDBService db = new MockDBService();
    private CountDownLatch lock = new CountDownLatch(1);
    private String title;
    private Quiz quiz;
    private List<Question> questions;
    private Error error;

    private Question bananaQuestion_en =
            new Question("Banana", "How many banana can you count ?", "matrix1x1");

    private void lockWait() {
        try {
            lock.await(2100, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

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
        assertEquals("I am a Mock Quiz!", title);
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
        assertTrue(questions.contains(bananaQuestion_en));
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
        assertTrue(quiz.getQuestions().contains(bananaQuestion_en));
        assertEquals("Title", quiz.getTitle());
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
}
