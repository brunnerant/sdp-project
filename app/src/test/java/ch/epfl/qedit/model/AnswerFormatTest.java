package ch.epfl.qedit.model;

import static org.junit.Assert.assertNull;

import ch.epfl.qedit.model.answer.AnswerFormat;
import ch.epfl.qedit.model.answer.MatrixFormat;
import org.junit.Test;

public class AnswerFormatTest {

    private static MatrixFormat defaultFormat(int numRows, int numColumns) {
        return MatrixFormat.uniform(numRows, numColumns, MatrixFormat.Field.textField(""));
    }

    private final AnswerFormat mat1x1 = defaultFormat(1, 1);
    private final AnswerFormat mat45x8 = defaultFormat(45, 8);

    @Test
    public void getTextTest() {
        assertNull(mat1x1.getText());
        assertNull(mat45x8.getText());
    }
}
