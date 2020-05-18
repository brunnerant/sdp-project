package ch.epfl.qedit.model;

import static org.junit.Assert.assertNotEquals;

import ch.epfl.qedit.model.answer.MatrixFormat;
import ch.epfl.qedit.model.answer.MultiFieldFormat;
import org.junit.Test;

public class MultiFieldFormatTest {

    private final MultiFieldFormat multi =
            new MultiFieldFormat(
                    MatrixFormat.uniform(3, 3, MatrixFormat.Field.textField("")),
                    MatrixFormat.singleField(MatrixFormat.Field.numericField(false, false, "")));

    @Test
    public void notEqualsTest1() {
        assertNotEquals(multi, "Not me");
        assertNotEquals(MatrixFormat.uniform(45, 1, MatrixFormat.Field.textField("")), multi);
    }

    @Test
    public void notEqualsTest2() {
        MultiFieldFormat multi1 =
                new MultiFieldFormat(
                        MatrixFormat.uniform(3, 3, MatrixFormat.Field.textField("")),
                        MatrixFormat.uniform(2, 1, MatrixFormat.Field.preFilledField("a")));
        assertNotEquals(multi1, multi);
        assertNotEquals(multi, multi1);
    }
}
