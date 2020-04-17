package ch.epfl.qedit.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;

import ch.epfl.qedit.model.answer.AnswerFormat;
import ch.epfl.qedit.model.answer.MatrixFormat;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;

public class QuizTest {

    private AnswerFormat answerFormat =
            MatrixFormat.singleField(MatrixFormat.Field.preFilledField(""));

    private List<Question> initQuestionList() {
        List<Question> questions = new ArrayList<>();
        questions.add(new Question("q1", "text", answerFormat));
        questions.add(new Question("q2", "text", answerFormat));
        questions.add(new Question("q3", "text", answerFormat));

        return questions;
    }

    @Test
    public void quizConstructorTest() {
        Quiz quiz = new Quiz("Title", initQuestionList());
        assertNotNull(quiz);
        assertEquals(3, quiz.getQuestions().size());
        assertEquals("q1", quiz.getQuestions().get(0).getTitle());
        assertEquals("text", quiz.getQuestions().get(1).getText());
    }

    @Test
    public void getQuestionIsImmutableTest() {
        Quiz quiz = new Quiz("Title", initQuestionList());
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
    public void getTitleTest() {
        Quiz quiz = new Quiz("About Math and Spider", initQuestionList());
        assertEquals(quiz.getTitle(), "About Math and Spider");
    }
}
