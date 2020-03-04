package ch.epfl.qedit.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class QuestionTest {
    @Test
    public void questionConstructorIsCorrect() {
        AnswerFormat f = new AnswerFormat.NumberField(0, 1, 0);
        Question q = new Question(0, "Question 1", "How old are you ?", f);

        assertEquals(q.getIndex(), 0);
        assertEquals(q.getTitle(), "Question 1");
        assertEquals(q.getText(), "How old are you ?");
        assertEquals(q.getFormat(), f);
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidQuestionsCannotBeBuilt1() {
        new Question(-1, "", "", new AnswerFormat.NumberField(0, 1, 0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidQuestionsCannotBeBuilt2() {
        new Question(0, null, "", new AnswerFormat.NumberField(0, 1, 0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidQuestionsCannotBeBuilt3() {
        new Question(0, "", null, new AnswerFormat.NumberField(0, 1, 0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidQuestionsCannotBeBuilt4() {
        new Question(0, "", "", null);
    }
}
