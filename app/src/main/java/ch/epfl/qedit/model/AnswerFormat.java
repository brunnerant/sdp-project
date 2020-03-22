package ch.epfl.qedit.model;

import java.io.Serializable;

/** This class represents all the answer formats that are available in the app. */
public abstract class AnswerFormat implements Serializable {

    @Override
    public abstract AnswerFormat clone();

    /** This method is used to implement the visitor pattern */
    public abstract void accept(Visitor visitor);

    public interface Visitor {

        void visitMatrixAnswerFormat(MatrixFormat matrixFormat);
    }
}
