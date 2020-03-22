package ch.epfl.qedit.util;

import ch.epfl.qedit.model.Question;
import ch.epfl.qedit.model.Quiz;
import java.util.ArrayList;
import java.util.Collection;

/** Builder for immutable Quizzes */
public class QuizBuilder {

    private ArrayList<Question> questions;
    private String title;

    public QuizBuilder() {
        questions = new ArrayList<>();
        title = "";
    }

    /** Append a copy of the question passed as argument to the Quiz */
    public QuizBuilder appendQuestion(Question question) {
        this.questions.add(question.clone());
        return this;
    }

    /** Append a collection of copy of questions passed as argument to the Quiz */
    public QuizBuilder appendQuestions(Collection<Question> questions) {
        ArrayList<Question> clonedQuestion = new ArrayList<>();
        for (Question q : questions) clonedQuestion.add(q.clone());

        this.questions.addAll(clonedQuestion);
        return this;
    }

    public QuizBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    /** Can only be called once */
    public Quiz build() {
        if (questions == null) throw new IllegalStateException();
        Quiz quiz = new Quiz(title, questions);
        questions = null;
        return quiz;
    }
}
