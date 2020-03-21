package ch.epfl.qedit.model;

import java.util.ArrayList;
import java.util.List;

public class QuizBuilder{

    private ArrayList<Question> questions;
    private String title;

    public QuizBuilder(){
        questions = new ArrayList<>();
        title = "";
    }

    public QuizBuilder appendQuestion(Question question){
        this.questions.add(question.clone());
        return this;
    }

    public QuizBuilder appendQuestions(List<Question> questions) {
        ArrayList<Question> clonedQuestion = new ArrayList<>();
        for(Question q: questions)
            clonedQuestion.add(q.clone());

        this.questions.addAll(clonedQuestion);
        return this;
    }

    public QuizBuilder setTitle(String title){
        this.title = title;
        return this;
    }

    public Quiz build(){
        if(questions == null)
            throw new IllegalMonitorStateException();
        Quiz quiz = new Quiz(title, questions);
        questions = null;
        return quiz;
    }
}
