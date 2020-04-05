package ch.epfl.qedit.model.answer;

import androidx.fragment.app.Fragment;

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
    public Fragment getAnswerFragment() {
        return new TestAnswerFragment();
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
