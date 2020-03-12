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
        questions.add(new Question(1, "q1", "text", new AnswerFormat.NumberField(10, 20, 17)));
        questions.add(new Question(2, "q2", "text", new AnswerFormat.NumberField(1, 20, 16)));
        questions.add(new Question(3, "q3", "text", new AnswerFormat.NumberField(10, 20, 18)));

        return questions;
    }

    @Test
    public void QuizConstructorTest() {
        Quiz quiz = new Quiz(initQuestionList());
        assertNotNull(quiz);
        assertEquals(quiz.getNbOfQuestions(), 3);
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

    @Test
    public void getQuestionReturnIsCorrectTest() {
        Quiz quiz = new Quiz(initQuestionList());
        List<Question> questions = quiz.getQuestions();
        assertEquals(questions.get(1).getTitle(), "q2");
    }
}
