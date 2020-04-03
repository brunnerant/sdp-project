package ch.epfl.qedit.model;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertThrows;

import ch.epfl.qedit.model.answer.MatrixModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;

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
                IndexOutOfBoundsException.class,
                new ThrowingRunnable() {
                    @Override
                    public void run() {
                        matrixModel.updateAnswer(3, 3, "nothing");
                    }
                });
    }

    @Test
    public void testGetAnswerThrowsException() {
        assertThrows(
                IndexOutOfBoundsException.class,
                new ThrowingRunnable() {
                    @Override
                    public void run() {
                        matrixModel.getAnswer(3, 3);
                    }
                });
    }

    @Test
    public void testAnswerIsUpdated() {
        matrixModel.updateAnswer(0, 0, "test");

        assertEquals("test", matrixModel.getAnswer(0, 0));
        assertEquals("", matrixModel.getAnswer(0, 1));
    }
}
