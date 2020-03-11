package ch.epfl.qedit.model;

import java.util.List;

public class Quiz {

    private List<Question> questions;

    public Quiz(List<Question> questions) {

        this.questions = questions;
    }

    public Question getQuestion(int index) {
        if (index < 0 || index >= getNbOfQuestions()) return null;
        return questions.get(index);
    }

    public int getNbOfQuestions() {
        return questions.size();
    }
}
