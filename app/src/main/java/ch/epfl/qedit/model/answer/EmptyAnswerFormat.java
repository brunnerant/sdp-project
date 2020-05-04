package ch.epfl.qedit.model.answer;

import ch.epfl.qedit.model.StringPool;
import ch.epfl.qedit.view.answer.AnswerFragment;
import ch.epfl.qedit.view.answer.EmptyAnswerFragment;

/** Allows to show a format with no content */
public class EmptyAnswerFormat extends AnswerFormat {
    public static final String EMPTY_FORMAT = "empty";

    public EmptyAnswerFormat(String text) {
        super(text);
    }

    @Override
    public AnswerModel getEmptyAnswerModel() {
        return new EmptyAnswerModel();
    }

    @Override
    public AnswerFragment<EmptyAnswerFormat, EmptyAnswerModel> getAnswerFragment() {
        return new EmptyAnswerFragment();
    }

    public static EmptyAnswerFormat parse(String format, String text) {
        if (format.trim().equals(EMPTY_FORMAT)) {
            return new EmptyAnswerFormat(null);
        } else {
            return null;
        }
    }

    @Override
    public AnswerFormat instantiateLanguage(StringPool pool) {
        return this;
    }
}
