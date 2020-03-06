package ch.epfl.qedit.model;

import java.util.List;

public class Quiz {

    private List<Question> questions;

    public Quiz(List<Question> questions) {

        this.questions = questions;
    }

    public void edit() {}

    public int getNbOfQuestions() {
        return questions.size();
    }
}
