package ch.epfl.qedit.model;

import static ch.epfl.qedit.model.Question.fromBundle;
import static org.junit.Assert.assertEquals;

import ch.epfl.qedit.util.BundledData;
import org.junit.Test;

public class QuestionTest {
    @Test
    public void questionConstructorIsCorrect() {
        AnswerFormat f = new MatrixFormat(1, 1);
        Question q = new Question("Question 1", "How old are you ?", f);

        assertEquals(q.getTitle(), "Question 1");
        assertEquals(q.getText(), "How old are you ?");
        assertEquals(q.getFormat(), f);
    }

    @Test(expected = NullPointerException.class)
    public void invalidQuestionsCannotBeBuilt2() {
        new Question(null, "", new MatrixFormat(1, 1));
    }

    @Test(expected = NullPointerException.class)
    public void invalidQuestionsCannotBeBuilt3() {
        new Question("", null, new MatrixFormat(1, 1));
    }

    @Test(expected = NullPointerException.class)
    public void invalidQuestionsCannotBeBuilt4() {
        new Question("", "", (AnswerFormat) null);
    }

    @Test(expected = NullPointerException.class)
    public void invalidQuestionsCannotBeBuilt() {
        new Question("", "", "");
    }

    @Test
    public void toBundleTest() {
        AnswerFormat f = new MatrixFormat(1, 1);
        Question q = new Question("Question 1", "How old are you ?", f);
        BundledData b = q.toBundle();
        assertEquals("Question 1", b.get("title"));
        assertEquals("How old are you ?", b.get("text"));
    }

    @Test
    public void fromBundleTest() {
        AnswerFormat f = new MatrixFormat(1, 1);
        Question q = new Question("Question 1", "How old are you ?", f);
        BundledData b = q.toBundle();
        assertEquals("Question 1", fromBundle(b).getTitle());
        assertEquals("How old are you ?", fromBundle(b).getText());
    }
}
