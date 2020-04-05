package ch.epfl.qedit.model.answer;

import java.io.Serializable;

import ch.epfl.qedit.util.Visitable;

/**
 * This class represents the answer that a user entered into a question. The subclasses of this
 * class are in a one-to-one correspondence with the types of answers represented by the
 * AnswerFormat class hierarchy.
 */
public abstract class AnswerModel implements Visitable<AnswerModel.Visitor>, Serializable {
    interface Visitor {
        void visitMatrixModel(MatrixModel matrixModel);

        void visitTestAnswerModel(TestAnswerModel testAnswerModel);

        void visitMultiFieldFormat(MultiFieldFormat multiFieldFormat);
    }
}
