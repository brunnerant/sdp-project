package ch.epfl.qedit.model.answer;

public class TestAnswerModel extends AnswerModel {
    @Override
    public void accept(Visitor visitor) {
        visitor.visitTestAnswerModel(this);
    }
}
