package ch.epfl.qedit.model;

import com.google.common.collect.ImmutableList;
import java.io.Serializable;
import java.util.List;

public class Quiz implements Serializable {

    private ImmutableList<Question> questions;

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
