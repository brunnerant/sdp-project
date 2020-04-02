package ch.epfl.qedit.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import ch.epfl.qedit.model.answer.AnswerFormat;
import ch.epfl.qedit.model.answer.MatrixFormat;
import ch.epfl.qedit.model.answer.MultiFieldFormat;
import java.util.Arrays;
import org.junit.Test;

public class AnswerFormatTest {

    private final AnswerFormat mat1x1 = new MatrixFormat("Enter number of solution here:", 1, 1);
    private final AnswerFormat mat45x8 = new MatrixFormat(45, 8);

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
    public void getTextTest() {
        assertEquals("Enter number of solution here:", mat1x1.getText());
        assertNull(mat45x8.getText());
    }

    @Test
    public void parseMultiField1() {
        AnswerFormat mat1x1 = new MatrixFormat(1, 1);
        AnswerFormat mat45x8 = new MatrixFormat(45, 8);
        assertEquals(
                new MultiFieldFormat(Arrays.asList(mat1x1, mat45x8)),
                AnswerFormat.parse("matrix1x1 ; matrix45x8"));
    }

    @Test
    public void parseMultiField2() {
        assertEquals(
                new MultiFieldFormat(Arrays.asList(mat1x1, mat45x8)),
                AnswerFormat.parse(
                        "matrix1x1 :     Enter number of solution here:    ; matrix45x8"));
    }

    @Test
    public void parseSingleField() {
        AnswerFormat mat1x1 = new MatrixFormat("Enter number of solution here:", 1, 1);
        assertEquals(
                mat1x1, AnswerFormat.parse("matrix1x1 :     Enter number of solution here:  "));
    }
}
