package ch.epfl.qedit.model;

import com.google.common.collect.ImmutableList;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/** Represents a quiz. For now, it is simply a immutable list of question. */
public class Quiz implements MultiLanguage<Quiz>, Serializable {
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
    public Quiz instantiateLanguage(StringPool pool) {
        String newTitle = pool.get(title);
        List<Question> newQuestions = new ArrayList<>(questions.size());

        for (Question q : questions) newQuestions.add(q.instantiateLanguage(pool));

        return new Quiz(newTitle, newQuestions);
    }
}
