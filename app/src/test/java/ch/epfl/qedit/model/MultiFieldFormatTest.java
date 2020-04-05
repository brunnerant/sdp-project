package ch.epfl.qedit.model;

import org.junit.Test;

import ch.epfl.qedit.model.answer.MatrixFormat;
import ch.epfl.qedit.model.answer.MultiFieldFormat;

import static org.junit.Assert.assertNotEquals;

public class MultiFieldFormatTest {

    private final MultiFieldFormat multi =
            new MultiFieldFormat(MatrixFormat.createMatrix3x3(), new MatrixFormat(1, 1));

    @Test
    public void notEqualsTest1() {
        assertNotEquals(multi, "Not me");
        assertNotEquals(new MatrixFormat(1, 45), multi);
    }

    @Test
    public void notEqualsTest2() {
        MultiFieldFormat multi1 =
                new MultiFieldFormat(MatrixFormat.createMatrix3x3(), new MatrixFormat(2, 1));
        assertNotEquals(multi1, multi);
        assertNotEquals(multi, multi1);
    }
}
