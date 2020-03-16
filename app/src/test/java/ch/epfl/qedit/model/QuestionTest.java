package ch.epfl.qedit.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class QuestionTest {
    @Test
    public void questionConstructorIsCorrect() {
        AnswerFormat f = new AnswerFormat.NumberField(0, 1, 0);
        Question q = new Question("Question 1", "How old are you ?", f);

        assertEquals(q.getTitle(), "Question 1");
        assertEquals(q.getText(), "How old are you ?");
        assertEquals(q.getFormat(), f);
    }

    @Test(expected = NullPointerException.class)
    public void invalidQuestionsCannotBeBuilt2() {
        new Question(null, "", new AnswerFormat.NumberField(0, 1, 0));
    }

    @Test(expected = NullPointerException.class)
    public void invalidQuestionsCannotBeBuilt3() {
        new Question("", null, new AnswerFormat.NumberField(0, 1, 0));
    }

    @Test(expected = NullPointerException.class)
    public void invalidQuestionsCannotBeBuilt4() {
        new Question("", "", null);
    }
}
