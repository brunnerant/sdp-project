package ch.epfl.qedit.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class QuizTest {

    private Quiz initQuizTest() {
        List<Question> questions = new ArrayList<>();
        questions.add(new Question(1, "q1", "text", new AnswerFormat.NumberField(10, 20, 17)));
        questions.add(new Question(2, "q2", "text", new AnswerFormat.NumberField(1, 20, 16)));
        questions.add(new Question(3, "q3", "text", new AnswerFormat.NumberField(10, 20, 18)));

        return new Quiz(questions);
    }

    @Test
    public void QuizConstructorTest() {
        Quiz quiz = initQuizTest();
        assertNotNull(quiz);
        assertEquals(quiz.getNbOfQuestions(), 3);
    }

    @Test
    public void getQuestionTestAtGoodIndex() {
        Quiz quiz = initQuizTest();
        assertNotNull(quiz.getQuestion(0));
        assertEquals(quiz.getQuestion(0).getTitle(), "q1");
    }

    @Test
    public void getQuestionTestAtWrongIndex() {
        Quiz quiz = initQuizTest();
        assertNull(quiz.getQuestion(-1));
        assertNull(quiz.getQuestion(quiz.getNbOfQuestions()));
        assertNull(quiz.getQuestion(quiz.getNbOfQuestions() + 100));
    }
}
