package ch.epfl.qedit.model;

import androidx.fragment.app.Fragment;
import ch.epfl.qedit.util.Visitable;
import ch.epfl.qedit.view.answer.MatrixFragment;
import ch.epfl.qedit.view.answer.TestAnswerFragment;
import java.io.Serializable;

/** This class represents all the answer formats that are available in the app. */
public abstract class AnswerFormat implements Visitable<AnswerFormat.Visitor>, Serializable {
    public interface Visitor {
        void visitMatrixFormat(MatrixFormat matrixFormat);

        void visitTestAnswerFormat(TestAnswerFormat testAnswerFormat);
    }

    // public abstract AnswerModel getAnswer();

    public AnswerModel emptyAnswerModel() {
        final AnswerModel[] answerModel = new AnswerModel[1];

        accept(
                new Visitor() {
                    @Override
                    public void visitMatrixFormat(MatrixFormat matrixFormat) {
                        answerModel[0] =
                                new MatrixModel(
                                        matrixFormat.getTableColumnsNumber(),
                                        matrixFormat.getTableRowsNumber());
                    }

                    @Override
                    public void visitTestAnswerFormat(TestAnswerFormat testAnswerFormat) {
                        answerModel[0] = new TestAnswerModel();
                    }
                });

        return answerModel[0];
    }

    public Fragment getFragment() {
        final Fragment[] fragment = new Fragment[1];

        accept(
                new Visitor() {
                    @Override
                    public void visitMatrixFormat(MatrixFormat matrixFormat) {
                        fragment[0] = new MatrixFragment();
                    }

                    @Override
                    public void visitTestAnswerFormat(TestAnswerFormat testAnswerFormat) {
                        fragment[0] = new TestAnswerFragment();
                    }
                });

        return fragment[0];
    }

    /**
     * Parse AnswerFormat from a string answer_format, call the override parse method in child
     * classes
     *
     * @param format string to parse into a AnswerFormat
     * @return AnswerFormat corresponding to the parsed format string, or null if the string is not
     *     a correct format
     */
    public static AnswerFormat parse(String format) {
        if (format == null) {
            return null;
        }
        return MatrixFormat.parse(format);
    }
}
