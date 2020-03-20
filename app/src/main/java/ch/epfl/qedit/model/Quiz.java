package ch.epfl.qedit.model;

import ch.epfl.qedit.util.Bundlable;
import ch.epfl.qedit.util.BundledData;
import com.google.common.collect.ImmutableList;
import java.util.List;

/** Represents a quiz. For now, it is simply a immutable list of question. */
public class Quiz implements Bundlable {
    /**
     * We cannot modify this list of question in the Quiz class, this list will be edited in a Quiz
     * builder
     */
    private final ImmutableList<Question> questions;

    private final String title;

    public Quiz(String title, List<Question> questions) {
        this.questions = ImmutableList.copyOf(questions);
        this.title = title;
    }

    public Quiz(List<Question> questions) {
        this.questions = ImmutableList.copyOf(questions);
        this.title = "Untitled Quiz";
    }

    public String getTitle() {
        return title;
    }

    public ImmutableList<Question> getQuestions() {
        return questions;
    }

    @Override
    public BundledData toBundle() {
        return new BundledData().update("questions", questions);
    }

    public static Quiz fromBundle(BundledData bundle) throws IllegalArgumentException {
        List<Question> questions = (List<Question>) bundle.get("questions");
        return new Quiz(questions);
    }
}
