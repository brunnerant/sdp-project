package ch.epfl.qedit.model;

import android.widget.Filterable;

import com.google.common.collect.ImmutableList;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Filter;

import ch.epfl.qedit.Search.Searchable;

/** Represents a quiz. For now, it is simply a immutable list of question. */
public class Quiz implements Serializable, Searchable<Quiz> {
    /**
     * We cannot modify this list of question in the Quiz class, this list will be edited in a Quiz
     * builder
     */
    private final List<Question> questions;

    private final String title;

    public Quiz(String title, List<Question> questions) {
        this.questions = new ArrayList<>(questions);
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public ImmutableList<Question> getQuestions() {
        return ImmutableList.copyOf(questions);
    }

    public void removeQuestionOnIndex(int index) {
        questions.remove(index);
    }

    @Override
    public Quiz search(String string, int position) {
        if (title.toLowerCase().contains(string)) {
            return this;
        }
        return null;
    }
}
