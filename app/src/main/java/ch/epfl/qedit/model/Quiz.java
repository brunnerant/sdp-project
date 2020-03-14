package ch.epfl.qedit.model;

import ch.epfl.qedit.util.Bundlable;
import ch.epfl.qedit.util.Bundle;
import com.google.common.collect.ImmutableList;
import java.io.Serializable;
import java.util.List;

/** Represents a quiz. For now, it is simply a immutable list of question. */
public class Quiz implements Bundlable, Serializable {
    /**
     * We cannot modify this list of question in the Quiz class, this list will be edited in a Quiz
     * builder
     */
    private final ImmutableList<Question> questions;

    public Quiz(List<Question> questions) {
        this.questions = ImmutableList.copyOf(questions);
    }

    public ImmutableList<Question> getQuestions() {
        return questions;
    }

    @Override
    public Bundle toBundle() {
        return new Bundle().update("questions", questions);
    }

    public static Quiz fromBundle(Bundle bundle) throws IllegalArgumentException {
        List<Question> questions = (List<Question>) bundle.get("questions");
        return new Quiz(questions);
    }
}
