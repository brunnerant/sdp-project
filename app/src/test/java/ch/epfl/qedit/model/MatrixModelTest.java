package ch.epfl.qedit.model;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertThrows;

import ch.epfl.qedit.model.answer.MatrixModel;
import org.junit.Before;
import org.junit.Test;

public class MatrixModelTest {
    private MatrixModel matrixModel;

    @Before
    public void setup() {
        matrixModel = new MatrixModel(2, 2);
    }

    @Test
    public void testInitializedEmpty() {
        assertEquals("", matrixModel.getAnswer(0, 0));
        assertEquals("", matrixModel.getAnswer(0, 1));
        assertEquals("", matrixModel.getAnswer(1, 0));
        assertEquals("", matrixModel.getAnswer(1, 1));
    }

    @Test
    public void testUpdateAnswerThrowsException() {
        assertThrows(
                IndexOutOfBoundsException.class, () -> matrixModel.updateAnswer(3, 3, "nothing"));
    }

    @Test
    public void testGetAnswerThrowsException() {
        assertThrows(IndexOutOfBoundsException.class, () -> matrixModel.getAnswer(3, 3));
    }

    @Test
    public void testAnswerIsUpdated() {
        matrixModel.updateAnswer(0, 0, "test");

        assertEquals("test", matrixModel.getAnswer(0, 0));
        assertEquals("", matrixModel.getAnswer(0, 1));
    }

    @Test
    public void testEqualsReturnsFalseWithNonMatrixModel() {
        MatrixModel matrix1 = new MatrixModel(1, 1);
        String test = "test";
        assertEquals(false, matrix1.equals(test));
    }

    @Test
    public void testEqualsWorksForSameModels() {
        MatrixModel matrixModel1 = new MatrixModel(1, 1);
        MatrixModel matrixModel2 = new MatrixModel(1, 1);
        matrixModel1.updateAnswer(0, 0, "test");
        matrixModel2.updateAnswer(0, 0, "test");

        assertEquals(true, matrixModel1.equals(matrixModel2));
        assertEquals(true, matrixModel2.equals(matrixModel1));
    }

    @Test
    public void testDifferentModelsNotEqual() {
        MatrixModel matrixModel1 = new MatrixModel(1, 1);
        MatrixModel matrixModel2 = new MatrixModel(1, 1);
        matrixModel1.updateAnswer(0, 0, "test1");
        matrixModel2.updateAnswer(0, 0, "test2");
        assertEquals(false, matrixModel1.equals(matrixModel2));
        assertEquals(false, matrixModel2.equals(matrixModel1));
    }

    @Test
    public void testDifferentSizeOfModelNotEqual() {
        MatrixModel matrixModel1 = new MatrixModel(1, 1);
        MatrixModel matrixModel2 = new MatrixModel(1, 2);

        matrixModel1.updateAnswer(0, 0, "test");
        matrixModel2.updateAnswer(0, 0, "test");
        matrixModel2.updateAnswer(0, 1, "test");
        assertEquals(false, matrixModel2.equals(matrixModel1));
    }
}
