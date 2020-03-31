package ch.epfl.qedit.model;

public class TestAnswerFormat extends AnswerFormat {

    @Override
    public void accept(Visitor visitor) {
        visitor.visitTestAnswerFormat(this);
    }
}
