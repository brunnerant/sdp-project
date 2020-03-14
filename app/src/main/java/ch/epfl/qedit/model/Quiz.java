package ch.epfl.qedit.model;

import com.google.common.collect.ImmutableList;
import java.io.Serializable;
import java.util.List;

/** Represents a quiz. For now, it is simply a immutable list of question. */
public class Quiz implements Serializable {

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

    public int getNbOfQuestions() {
        return questions.size();
    }
}
