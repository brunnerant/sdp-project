package ch.epfl.qedit.model;

import com.google.common.collect.ImmutableList;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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

    public static class Builder implements Serializable {

        private List<Question> questions;

        public Builder() {
            questions = new ArrayList<>();
        }

        public Builder(Quiz quiz) {
            questions = new ArrayList<>(quiz.getQuestions());
        }

        public Builder add(Question question) {
            checkState();
            questions.add(question);
            return this;
        }

        public Builder add(int index, Question question) {
            checkState();
            questions.add(index, question);
            return this;
        }

        public Builder addEmptyQuestion() {
            checkState();
            questions.add(new Question.EmptyQuestion());
            return this;
        }

        public Builder update(int index, Question question) {
            checkState();
            if (!question.isEmpty()) {
                questions.set(index, question);
            }
            return this;
        }

        public Builder swap(int index1, int index2) {
            checkState();
            Question tmp = questions.get(index1);
            questions.set(index1, questions.get(index2));
            questions.set(index2, tmp);
            return this;
        }

        public Builder remove(int index) {
            checkState();
            if (index >= 0 && index < questions.size()) {
                questions.remove(index);
            }
            return this;
        }

        public ImmutableList<Question> getQuestions() {
            checkState();
            return ImmutableList.copyOf(questions);
        }

        public int size() {
            checkState();
            return questions.size();
        }

        public Quiz build() {
            checkState();
            // TODO check that no empty questions
            List<Question> quizQuestion = questions;
            questions = null;
            return new Quiz(StringPool.TITLE_ID, quizQuestion);
        }

        private void checkState() {
            if (questions == null) {
                throw new IllegalStateException("Builder already build once.");
            }
        }
    }
}
