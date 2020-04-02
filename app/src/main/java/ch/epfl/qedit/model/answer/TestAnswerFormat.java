package ch.epfl.qedit.model.answer;

public class TestAnswerFormat extends AnswerFormat {
    public TestAnswerFormat(String text) {
        super(text);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitTestAnswerFormat(this);
    }

    public static TestAnswerFormat parse(String format, String text) {
        if (format.trim().equals("testAnswerFormat")) {
            return new TestAnswerFormat(null);
        } else {
            return null;
        }
    }
}
