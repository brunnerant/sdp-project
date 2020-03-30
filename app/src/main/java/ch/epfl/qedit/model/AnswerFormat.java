package ch.epfl.qedit.model;

import java.io.Serializable;

/** This class represents all the answer formats that are available in the app. */
public abstract class AnswerFormat implements Serializable {

    /** This method is used to implement the visitor pattern */
    public abstract void accept(Visitor visitor);

    public interface Visitor {

        void visitMatrixAnswerFormat(MatrixFormat matrixFormat);
    }

    public abstract void saveAnswers();

    public abstract AnswerModel getAnswers();

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
