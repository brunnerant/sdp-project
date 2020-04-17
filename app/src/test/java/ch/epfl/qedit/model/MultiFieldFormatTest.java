package ch.epfl.qedit.model;

import static org.junit.Assert.assertNotEquals;

import ch.epfl.qedit.model.answer.MatrixFormat;
import ch.epfl.qedit.model.answer.MultiFieldFormat;
import org.junit.Test;

public class MultiFieldFormatTest {

    private final MultiFieldFormat multi =
            new MultiFieldFormat(
                    MatrixFormat.uniform(3, 3, MatrixFormat.Field.textField("", 1)),
                    MatrixFormat.singleField(MatrixFormat.Field.numericField(false, false, "", 3)));

    @Test
    public void notEqualsTest1() {
        assertNotEquals(multi, "Not me");
        assertNotEquals(MatrixFormat.uniform(45, 1, MatrixFormat.Field.textField("", 42)), multi);
    }

    @Test
    public void notEqualsTest2() {
        MultiFieldFormat multi1 =
                new MultiFieldFormat(
                        MatrixFormat.uniform(3, 3, MatrixFormat.Field.textField("", 1)),
                        MatrixFormat.uniform(2, 1, MatrixFormat.Field.preFilledField("a")));
        assertNotEquals(multi1, multi);
        assertNotEquals(multi, multi1);
    }
}
