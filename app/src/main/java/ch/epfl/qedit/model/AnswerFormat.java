package ch.epfl.qedit.model;

import java.io.Serializable;

/** This class represents all the answer formats that are available in the app. */
public abstract class AnswerFormat implements Serializable {

    /** This method is used to implement the visitor pattern */
    public abstract void accept(Visitor visitor);

    public interface Visitor {

        void visitMatrixAnswerFormat(MatrixFormat matrixFormat);
    }

    /**
     * Parse AnswerFormat from a string answer_format, call the parse method override in child
     * classes
     */
    public static AnswerFormat parse(String format) {
        if (format == null) return null;
        return MatrixFormat.parse(format);
    }
}
