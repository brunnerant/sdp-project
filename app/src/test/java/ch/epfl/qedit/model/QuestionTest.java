package ch.epfl.qedit.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import ch.epfl.qedit.model.answer.AnswerFormat;
import ch.epfl.qedit.model.answer.MatrixFormat;
import org.junit.Test;

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
        Question q4 = new Question("Question 2", "How old are you?", "matrix1x1");

        assertEquals(q1, q2);
        assertNotEquals(q1, "Pomme de Terre");
        assertNotEquals(q1, q4);
    }

    @Test
    public void isEmptyTest() {
        Question nonEmpty = new Question("Non empty", "This question is not empty", "matrix1x1");
        Question empty = new Question.Empty();

        assertFalse(nonEmpty.isEmpty());
        assertTrue(empty.isEmpty());
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

    // BUILDER TESTS //
    private Question.Builder initBuilder() {
        Question.Builder builder = new Question.Builder();
        return builder.setTitleID(TITLE_ID).setTextID(TEXT_ID).setFormat(answer);
    }

    @Test
    public void builderTest() {
        Question q0 = new Question(TITLE_ID, TEXT_ID, answer);
        Question q1 = initBuilder().build();
        Question q2 = (new Question.Builder(q0)).build();
        assertEquals(q0, q1);
        assertEquals(q0, q2);
        assertEquals(q1, q2);
    }

    @Test(expected = IllegalStateException.class)
    public void builderBuildFail0Test() {
        Question.Builder builder = new Question.Builder();
        builder.build();
    }

    @Test(expected = IllegalStateException.class)
    public void builderBuildFail1Test() {
        Question.Builder builder = initBuilder();
        builder.build();
        builder.build();
    }

    @Test(expected = IllegalStateException.class)
    public void builderBuildFail2Test() {
        Question.Builder builder = initBuilder();
        builder.build();
        builder.setTitleID("ID1");
    }

    @Test(expected = IllegalStateException.class)
    public void builderBuildFail3Test() {
        Question.Builder builder = initBuilder();
        builder.build();
        builder.setTextID("ID1");
    }

    @Test(expected = IllegalStateException.class)
    public void builderBuildFail4Test() {
        Question.Builder builder = initBuilder();
        builder.build();
        builder.setFormat(answer);
    }
}
