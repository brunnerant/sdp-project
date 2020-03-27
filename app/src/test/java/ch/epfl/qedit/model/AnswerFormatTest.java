package ch.epfl.qedit.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class AnswerFormatTest {

    @Test
    public void parseWrong() {
        //noinspection SpellCheckingInspection
        assertNull(AnswerFormat.parse("iufbziub"));
        assertNull(AnswerFormat.parse("matrix45x8x6"));
        assertNull(AnswerFormat.parse(""));
        assertNull(AnswerFormat.parse(null));
    }

    @Test
    public void parseMatrix() {
        assertEquals(new MatrixFormat(1, 1), AnswerFormat.parse("matrix1x1"));
    }
}
