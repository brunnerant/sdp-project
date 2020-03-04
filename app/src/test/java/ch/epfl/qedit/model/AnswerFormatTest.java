package ch.epfl.qedit.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class AnswerFormatTest {
    @Test
    public void answerCanBeCorrectlyVisited() {
        final AnswerFormat.NumberField format = new AnswerFormat.NumberField(0, 1, 0);
        format.accept(
                new AnswerFormat.Visitor() {
                    @Override
                    public void visitNumberField(AnswerFormat.NumberField field) {
                        assertEquals(field.getMin(), format.getMin(), 0);
                        assertEquals(field.getMax(), format.getMax(), 0);
                        assertEquals(field.getDigits(), format.getDigits());
                    }
                });
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidNumberFieldCannotBeBuilt1() {
        new AnswerFormat.NumberField(0, -1, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidNumberFieldCannotBeBuilt2() {
        new AnswerFormat.NumberField(0, 1, -1);
    }
}
