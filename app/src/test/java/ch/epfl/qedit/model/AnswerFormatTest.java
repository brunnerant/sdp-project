package ch.epfl.qedit.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import ch.epfl.qedit.model.answer.AnswerFormat;
import ch.epfl.qedit.model.answer.MatrixFormat;
import ch.epfl.qedit.model.answer.MultiFieldFormat;
import java.util.Arrays;
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

    @Test
    public void parseMultiField() {
        AnswerFormat mat1x1 = new MatrixFormat(1, 1);
        AnswerFormat mat45x8 = new MatrixFormat(45, 8);
        assertEquals(
                new MultiFieldFormat(Arrays.asList(mat1x1, mat45x8)),
                AnswerFormat.parse("matrix1x1 ; matrix45x8"));
    }
}
