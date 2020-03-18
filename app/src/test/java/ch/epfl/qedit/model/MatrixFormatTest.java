package ch.epfl.qedit.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MatrixFormatTest {
    @Test
    public void checkMatrixSingleField() {
        MatrixFormat matrixFormat = MatrixFormat.createSingleField(true, true, 5);

        checkAllElements(matrixFormat, true, true, 1, 1, 5, "00000");
    }

    @Test
    public void checkSetHint() {
        MatrixFormat matrixFormat = MatrixFormat.createSingleField(true, true, 5);
        matrixFormat.setHint("00");
        checkAllElements(matrixFormat, true, true, 1, 1, 5, "00");
    }

    @Test
    public void checkMatrix3x3Normal() {
        MatrixFormat matrixFormat = MatrixFormat.createMatrix3x3();

        checkAllElements(matrixFormat, true, true, 3, 3, 5, "00000");
    }

    @Test
    public void checkMatrix3x3WithParam() {
        MatrixFormat matrixFormat = MatrixFormat.createMatrix3x3(true, true, 5);
        ;

        checkAllElements(matrixFormat, true, true, 3, 3, 5, "00000");
    }

    private void checkAllElements(
            MatrixFormat matrixFormat,
            boolean hasDecimal,
            boolean hasSign,
            int tableRowsNumber,
            int tableColumnsNumber,
            int maxCharacters,
            String hintString) {
        assertEquals(matrixFormat.hasDecimal(), hasDecimal);
        assertEquals(matrixFormat.hasSign(), hasSign);
        assertEquals(matrixFormat.getTableColumnsNumber(), tableColumnsNumber);
        assertEquals(matrixFormat.getTableRowsNumber(), tableRowsNumber);
        assertEquals(matrixFormat.getTableRowsNumber(), tableRowsNumber);
        assertEquals(matrixFormat.getMaxCharacters(), maxCharacters);
        assertEquals(matrixFormat.getHint(), hintString);
    }
}
