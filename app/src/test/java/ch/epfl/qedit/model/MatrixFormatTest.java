package ch.epfl.qedit.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;

import androidx.fragment.app.Fragment;
import ch.epfl.qedit.model.answer.AnswerModel;
import ch.epfl.qedit.model.answer.MatrixFormat;
import ch.epfl.qedit.model.answer.MatrixModel;
import ch.epfl.qedit.view.answer.MatrixFragment;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;

public class MatrixFormatTest {

    private final MatrixFormat.Field field = MatrixFormat.Field.preFilledField("");

    @Test
    public void testFieldEquals() {
        assertEquals(field, MatrixFormat.Field.preFilledField(""));
        assertNotEquals(field, null);
        assertNotEquals(null, field);
        assertNotEquals(field, "string");
        assertNotEquals(field, MatrixFormat.Field.textField(""));
    }

    @Test
    public void testMatrixFormatEquals() {
        MatrixFormat.Field a = MatrixFormat.Field.preFilledField("a");
        MatrixFormat.Field b = MatrixFormat.Field.preFilledField("b");

        MatrixFormat m1 = new MatrixFormat.Builder(2, 2).withField(0, 0, a).build();
        MatrixFormat m2 = new MatrixFormat.Builder(2, 2).build();
        MatrixFormat m3 = new MatrixFormat.Builder(2, 2).build();
        assertNotEquals(m1, m2);
        assertEquals(m2, m3);

        m1 = new MatrixFormat.Builder(1, 1).withField(0, 0, a).withField(0, 0, b).build();
        m2 = new MatrixFormat.Builder(1, 1).withField(0, 0, b).build();
        assertEquals(m1, m2);
    }

    @Test
    public void testMatrixFormatBuilderCrashes() {
        assertThrows(
                IllegalArgumentException.class,
                new ThrowingRunnable() {
                    @Override
                    public void run() throws Throwable {
                        new MatrixFormat.Builder(-1, 1);
                    }
                });

        assertThrows(
                IllegalStateException.class,
                new ThrowingRunnable() {
                    @Override
                    public void run() throws Throwable {
                        MatrixFormat.Builder b = new MatrixFormat.Builder(1, 1);
                        b.build();
                        b.build();
                    }
                });
    }

    @Test
    public void testFragmentIsCorrectlyDispatched() {
        MatrixFormat matrixFormat = MatrixFormat.singleField(field);
        Fragment fragment = matrixFormat.getAnswerFragment();
        assertEquals(MatrixFragment.class, fragment.getClass());
    }

    @Test
    public void testModelIsCorrectlyDispatched() {
        MatrixFormat matrixFormat = MatrixFormat.singleField(field);
        AnswerModel answerModel = matrixFormat.getEmptyAnswerModel();
        assertEquals(MatrixModel.class, answerModel.getClass());
    }
}
