package ch.epfl.qedit.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class QuizTest {
    @Test
    public void QuizConstructorTest() {
        List<Question> questions = new ArrayList<>();
        questions.add(new Question(10, "titre", "text", new AnswerFormat.NumberField(10, 20, 17)));
        questions.add(new Question(10, "titre", "text", new AnswerFormat.NumberField(1, 20, 16)));
        questions.add(new Question(10, "titre", "text", new AnswerFormat.NumberField(10, 20, 18)));

        Quiz quiz = new Quiz(questions);
        assertNotNull(quiz);
        assertEquals(quiz.getNbOfQuestions(), 3);
    }
}
