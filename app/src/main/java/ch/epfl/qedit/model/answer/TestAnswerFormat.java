package ch.epfl.qedit.model.answer;

import ch.epfl.qedit.model.StringPool;
import ch.epfl.qedit.view.answer.AnswerFragment;
import ch.epfl.qedit.view.answer.TestAnswerFragment;

/** Only for test purposes */
public class TestAnswerFormat extends AnswerFormat {
    public TestAnswerFormat(String text) {
        super(text);
    }

    @Override
    public AnswerModel getEmptyAnswerModel() {
        return new TestAnswerModel();
    }

    @Override
    public AnswerFragment<TestAnswerFormat, TestAnswerModel> getAnswerFragment() {
        return new TestAnswerFragment();
    }

    public static TestAnswerFormat parse(String format, String text) {
        if (format.trim().equals("testAnswerFormat")) {
            return new TestAnswerFormat(null);
        } else {
            return null;
        }
    }

    @Override
    public AnswerFormat instantiateLanguage(StringPool pool) {
        return this;
    }
}
