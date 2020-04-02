package ch.epfl.qedit.model.answer;

import androidx.fragment.app.Fragment;
import ch.epfl.qedit.util.Visitable;
import ch.epfl.qedit.view.answer.MatrixFragment;
import ch.epfl.qedit.view.answer.TestAnswerFragment;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

/** This class represents all the answer formats that are available in the app. */
public abstract class AnswerFormat
        implements Visitable<ch.epfl.qedit.model.answer.AnswerFormat.Visitor>, Serializable {

    // Can be null
    private String text;

    // Package Private: protected
    AnswerFormat(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof AnswerFormat) {
            AnswerFormat other = (AnswerFormat) o;
            return Objects.equals(this.text, other.text);
        }
        return false;
    }

    public interface Visitor {
        void visitMatrixFormat(MatrixFormat matrixFormat);

        void visitTestAnswerFormat(TestAnswerFormat testAnswerFormat);

        void visitMultiFieldFormat(MultiFieldFormat multiFieldFormat);
    }

    // public abstract AnswerModel getModel(); //TODO

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

                    @Override
                    public void visitMultiFieldFormat(MultiFieldFormat multiFieldFormat) {
                        // TODO
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

                    @Override
                    public void visitMultiFieldFormat(MultiFieldFormat multiFieldFormat) {
                        // TODO
                    }
                });

        return fragment[0];
    }

    private static AnswerFormat parseNotCompoundFormat(String field) {

        // Split <format>:<text> into two string
        String[] formatAndText = field.split(":", 2);
        String format = formatAndText[0];
        String text = (formatAndText.length == 2) ? formatAndText[1].trim() : null;

        AnswerFormat answerFormat = MatrixFormat.parse(format, text);

        if (answerFormat == null) {
            answerFormat = TestAnswerFormat.parse(format, text);
        }

        return answerFormat;
    }

    /**
     * Parse AnswerFormat from a string answerFormat, call the override parse method in child
     * classes
     *
     * @param answerFormat string to parse into a AnswerFormat
     * @return AnswerFormat corresponding to the parsed format string, or null if the string is not
     *     a correct format
     */
    public static AnswerFormat parse(String answerFormat) {
        if (answerFormat == null) {
            return null;
        }
        // Parse answer format:  <format>:<text> ; <format> ; ... ; <format>:<text>
        ArrayList<AnswerFormat> fields = new ArrayList<>();
        for (String field : answerFormat.split(";")) {
            // <format>:<text> | <format>
            AnswerFormat result = parseNotCompoundFormat(field);
            if (result == null) {
                return null;
            }
            fields.add(result);
        }

        return (fields.size() == 1) ? fields.get(0) : new MultiFieldFormat(fields);
    }
}
