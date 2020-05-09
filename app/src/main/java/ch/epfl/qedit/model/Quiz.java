package ch.epfl.qedit.model;

import ch.epfl.qedit.search.Searchable;
import com.google.common.collect.ImmutableList;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/** Represents a quiz. For now, it is simply a immutable list of question. */
public final class Quiz implements MultiLanguage<Quiz>, Serializable, Searchable<Quiz> {
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

    /** A Quiz Builder use to build a Quiz step by step, essentially useful in Quiz edition */
    public static class Builder implements Serializable {

        private List<Question> questions;

        public Builder() {
            questions = new ArrayList<>();
        }

        /** Useful when we want to modify a quiz already existent */
        public Builder(Quiz quiz) {
            questions = new ArrayList<>(quiz.getQuestions());
        }

        /** Append a question the quiz's list of question */
        public Builder append(Question question) {
            checkState();
            questions.add(question);
            return this;
        }

        /** Put a question at a certain index in the quiz */
        public Builder insert(int index, Question question) {
            checkState();
            questions.add(index, question);
            return this;
        }

        /** Append an empty question to the Quiz */
        public Builder addEmptyQuestion() {
            checkState();
            questions.add(new Question.Empty());
            return this;
        }

        /** Change the question at a certain index */
        public Builder update(int index, Question question) {
            checkState();
            if (!question.isEmpty()) {
                questions.set(index, question);
            }
            return this;
        }

        /** swap questions at index index1 and index2 */
        public Builder swap(int index1, int index2) {
            checkState();
            Question tmp = questions.get(index1);
            questions.set(index1, questions.get(index2));
            questions.set(index2, tmp);
            return this;
        }

        /** remove question from the quiz */
        public Builder remove(int index) {
            checkState();
            if (index >= 0 && index < questions.size()) {
                questions.remove(index);
            }
            return this;
        }

        /** return the current list of question in the builder */
        public ImmutableList<Question> getQuestions() {
            checkState();
            return ImmutableList.copyOf(questions);
        }

        /** return the current number of question in the Quiz */
        public int size() {
            checkState();
            return questions.size();
        }

        /**
         * Build a quiz from the current state of the builder, invalid the state of the builder so
         * that it only be called once
         */
        public Quiz build() {
            checkState();
            List<Question> quizQuestion = questions;
            questions = null;
            return new Quiz(StringPool.TITLE_ID, quizQuestion);
        }

        /** check if the builder is valid i.e. build() has not been called yet */
        private void checkState() {
            if (questions == null) {
                throw new IllegalStateException("Builder already build once.");
            }
        }
    }

    @Override
    public Quiz instantiateLanguage(StringPool pool) {
        String newTitle = pool.get(title);
        List<Question> newQuestions = new ArrayList<>(questions.size());

        for (Question q : questions) newQuestions.add(q.instantiateLanguage(pool));

        return new Quiz(newTitle, newQuestions);
    }

    @Override
    public Quiz search(String string, int position) {
        if (title.toLowerCase().contains(string)) {
            return this;
        }
        return null;
    }
}
