package ch.epfl.qedit.model;

import android.location.Location;

import org.junit.Test;

import ch.epfl.qedit.model.answer.AnswerFormat;
import ch.epfl.qedit.model.answer.MatrixFormat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

public class QuestionTest {

    private MatrixFormat format =
            MatrixFormat.singleField(MatrixFormat.Field.textField("", MatrixFormat.Field.NO_LIMIT));

    private final String TITLE_ID = "ID0", TEXT_ID = "ID1", PRE_FILLED_ID = "ID2";
    private final MatrixFormat answer =
            MatrixFormat.singleField(MatrixFormat.Field.preFilledField(PRE_FILLED_ID));

    @Test
    public void questionConstructorIsCorrect() {
        Question q = new Question("Question 1", "How old are you?", format);

        assertEquals(q.getTitle(), "Question 1");
        assertEquals(q.getText(), "How old are you?");
        assertEquals(q.getFormat(), format);
        assertNull(q.getLocation());
        assertEquals(q.getRadius(), -1);
    }

    @Test
    public void questionConstructorIsCorrect2() {
        Location loc = new Location("");
        Question q = new Question("Question 1", "How old are you?", format, loc, 100);

        assertEquals(q.getTitle(), "Question 1");
        assertEquals(q.getText(), "How old are you?");
        assertEquals(q.getFormat(), format);
        assertEquals(q.getLocation(), loc);
        assertEquals(q.getRadius(), 100);
    }

    @Test
    public void questionEqualsTest() {
        Question q1 = new Question("Question 1", "How old are you?", format);
        Question q2 = new Question("Question 1", "How old are you?", "matrix1x1");
        Question q4 = new Question("Question 2", "How old are you?", "matrix1x1");

        assertEquals(q1, q2);
        assertNotEquals(q1, "Pomme de Terre");
        assertNotEquals(q1, q4);
    }

    @Test(expected = NullPointerException.class)
    public void invalidQuestionsCannotBeBuilt1() {
        new Question("", "", "");
    }

    @Test(expected = NullPointerException.class)
    public void invalidQuestionsCannotBeBuilt2() {
        new Question(null, "", format);
    }

    @Test(expected = NullPointerException.class)
    public void invalidQuestionsCannotBeBuilt3() {
        new Question("", null, format);
    }

    @Test(expected = NullPointerException.class)
    public void invalidQuestionsCannotBeBuilt4() {
        new Question("", "", (AnswerFormat) null);
    }

    @Test(expected = NullPointerException.class)
    public void invalidQuestionsCannotBeBuilt5() {
        new Question("", "", format, null, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidQuestionsCannotBeBuilt6() {
        new Question("", "", format, new Location(""), -1);
    }
}
