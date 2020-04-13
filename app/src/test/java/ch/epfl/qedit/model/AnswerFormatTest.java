package ch.epfl.qedit.model;


import org.junit.Test;

import java.util.Arrays;

import ch.epfl.qedit.model.answer.AnswerFormat;
import ch.epfl.qedit.model.answer.MatrixFormat;
import ch.epfl.qedit.model.answer.MultiFieldFormat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class AnswerFormatTest {

    private static MatrixFormat defaultFormat(int numRows, int numColumns) {
        return MatrixFormat.uniform(numRows, numColumns, MatrixFormat.Field.textField("", MatrixFormat.Field.NO_LIMIT));
    }

    private final AnswerFormat mat1x1 = defaultFormat(1, 1);
    private final AnswerFormat mat45x8 = defaultFormat(45, 8);

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
        assertEquals(mat1x1, AnswerFormat.parse("matrix1x1"));
    }

    @Test
    public void getTextTest() {
        assertNull(mat1x1.getText());
        assertNull(mat45x8.getText());
    }

    @Test
    public void parseMultiField1() {
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
        assertEquals(
                mat1x1, AnswerFormat.parse("matrix1x1 :     Enter number of solution here:  "));
    }
}
