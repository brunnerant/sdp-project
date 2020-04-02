package ch.epfl.qedit.model.answer;

import ch.epfl.qedit.util.Visitable;
import java.io.Serializable;

/** This is the abstract super class of all different AnswerModels the we are going to implement */
public abstract class AnswerModel implements Visitable<AnswerModel.Visitor>, Serializable {
    interface Visitor {
        void visitMatrixModel(MatrixModel matrixModel);

        void visitTestAnswerModel(TestAnswerModel testAnswerModel);

        void visitMultiFieldFormat(MultiFieldFormat multiFieldFormat);
    }
}
