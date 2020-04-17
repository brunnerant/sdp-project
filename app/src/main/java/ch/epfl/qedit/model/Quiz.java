package ch.epfl.qedit.model;

import com.google.common.collect.ImmutableList;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Represents a quiz. For now, it is simply a immutable list of question. */
public final class Quiz implements Serializable {
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

    public static class Builder{

        private List<Question> questions;

        public Builder(){
            questions = new ArrayList<>();
        }

        public ImmutableList<Question> getQuestions(){
            return ImmutableList.copyOf(questions);
        }

        public Quiz build(){
            if(questions == null){
                throw new IllegalStateException();
            }
            List<Question> quizQuestion = questions;
            questions = null;
            return new Quiz(null, quizQuestion);
        }

    }
}
