package ch.epfl.qedit.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;

public class QuizTest {

    private List<Question> initQuestionList() {
        List<Question> questions = new ArrayList<>();
        questions.add(new Question("q1", "text", new AnswerFormat.NumberField(10, 20, 17)));
        questions.add(new Question("q2", "text", new AnswerFormat.NumberField(1, 20, 16)));
        questions.add(new Question("q3", "text", new AnswerFormat.NumberField(10, 20, 18)));

        return questions;
    }

    @Test
    public void quizConstructorTest() {
        Quiz quiz = new Quiz(initQuestionList());
        assertNotNull(quiz);
        assertEquals(3, quiz.getQuestions().size());
        assertEquals("q1", quiz.getQuestions().get(0).getTitle());
        assertEquals("text", quiz.getQuestions().get(1).getText());
    }

    @Test
    public void getQuestionIsImmutableTest() {
        Quiz quiz = new Quiz(initQuestionList());
        final List<Question> questions = quiz.getQuestions();
        assertThrows(
                UnsupportedOperationException.class,
                new ThrowingRunnable() {
                    @Override
                    public void run() {
                        questions.add(null);
                    }
                });
    }
}
