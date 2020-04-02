package ch.epfl.qedit.model.answer;

/** Only for test purposes */
public class TestAnswerModel extends AnswerModel {
    @Override
    public void accept(Visitor visitor) {
        visitor.visitTestAnswerModel(this);
    }
}
