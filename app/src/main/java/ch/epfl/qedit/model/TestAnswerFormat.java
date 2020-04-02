package ch.epfl.qedit.model;

public class TestAnswerFormat extends AnswerFormat {

    @Override
    public void accept(Visitor visitor) {
        visitor.visitTestAnswerFormat(this);
    }

    public static TestAnswerFormat parse(String format) {
        if (format.trim() == "testAnswerFormat") {
            return new TestAnswerFormat();
        } else {
            return null;
        }
    }
}
