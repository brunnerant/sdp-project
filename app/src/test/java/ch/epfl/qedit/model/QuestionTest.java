package ch.epfl.qedit.model;

import org.junit.Test;

import ch.epfl.qedit.model.answer.AnswerFormat;
import ch.epfl.qedit.model.answer.MatrixFormat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class QuestionTest {

    private MatrixFormat format = MatrixFormat.singleField(MatrixFormat.Field.textField("", MatrixFormat.Field.NO_LIMIT));

    @Test
    public void questionConstructorIsCorrect() {
        Question q = new Question("Question 1", "How old are you?", format);

        assertEquals(q.getTitle(), "Question 1");
        assertEquals(q.getText(), "How old are you?");
        assertEquals(q.getFormat(), format);
    }

    @Test
    public void questionConstructorIsCorrect2() {
        Question q = new Question("Question 1", "How old are you?", "matrix1x1");

        assertEquals(q.getTitle(), "Question 1");
        assertEquals(q.getText(), "How old are you?");
        assertEquals(q.getFormat(), format);
    }

    @Test
    public void questionEqualsTest() {
        Question q1 = new Question("Question 1", "How old are you?", format);
        Question q2 = new Question("Question 1", "How old are you?", "matrix1x1");
        Question q3 = new Question("Question 1", "How old are you?", "matrix7x1");
        Question q4 = new Question("Question 2", "How old are you?", "matrix1x1");

        assertEquals(q1, q2);
        assertNotEquals(q1, "Pomme de Terre");
        assertNotEquals(q1, q4);
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
    public void invalidQuestionsCannotBeBuilt() {
        new Question("", "", "");
    }
}
