package ch.epfl.qedit.model;

import ch.epfl.qedit.util.Visitable;
import java.io.Serializable;

public abstract class AnswerModel implements Visitable<AnswerModel.Visitor>, Serializable {
    public interface Visitor {
        void visitMatrixModel(MatrixModel matrixModel);

        void visitTestAnswerModel(TestAnswerModel testAnswerModel);
    }
}
