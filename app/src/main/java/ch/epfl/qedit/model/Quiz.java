package ch.epfl.qedit.model;

import com.google.common.collect.ImmutableList;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/** Represents a quiz. For now, it is simply a immutable list of question. */
public final class Quiz implements MultiLanguage<Quiz>, Serializable {
    /**
     * We cannot modify this list of question in the Quiz class, this list will be edited in a Quiz
     * builder
     */
    private final List<Question> questions;

    private final String title;

    /** This flag indicates whether the quiz is a treasure hunt quiz */
    private final boolean treasureHunt;

    /** This can be used to construct generic quizzes */
    public Quiz(String title, List<Question> questions, boolean treasureHunt) {
        this.questions = new ArrayList<>(questions);
        this.title = title;
        this.treasureHunt = treasureHunt;
    }

    /** This constructs a non treasure-hunt quiz */
    public Quiz(String title, List<Question> questions) {
        this(title, questions, false);
    }

    public String getTitle() {
        return title;
    }

    public ImmutableList<Question> getQuestions() {
        return ImmutableList.copyOf(questions);
    }

    public boolean isTreasureHunt() {
        return treasureHunt;
    }

    /** A Quiz Builder use to build a Quiz step by step, essentially useful in Quiz edition */
    public static class Builder implements Serializable {

        private List<Question> questions;
        private boolean treasureHunt;

        /** This is the used to create a generic quiz builder */
        public Builder(boolean treasureHunt) {
            this.questions = new ArrayList<>();
            this.treasureHunt = treasureHunt;
        }

        /** This is used to create a non treasure-hunt quiz builder */
        public Builder() {
            this(false);
        }

        /** Useful when we want to modify a quiz already existent */
        public Builder(Quiz quiz) {
            this.questions = new ArrayList<>(quiz.getQuestions());
            this.treasureHunt = quiz.treasureHunt;
        }

        /** Append a question the quiz's list of question */
        public Builder append(Question question) {
            checkState();
            checkQuestion(question);

            questions.add(question);
            return this;
        }

        /** Put a question at a certain index in the quiz */
        public Builder insert(int index, Question question) {
            checkState();
            checkQuestion(question);

            questions.add(index, question);
            return this;
        }

        /** Change the question at a certain index */
        public Builder update(int index, Question question) {
            checkState();
            checkQuestion(question);

            questions.set(index, question);
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
            return new Quiz(StringPool.TITLE_ID, quizQuestion, treasureHunt);
        }

        /** Checks if the builder is valid i.e. build() has not been called yet */
        private void checkState() {
            if (questions == null) {
                throw new IllegalStateException("Builder already build once.");
            }
        }

        /** Checks that the question has the same treasure-hunt settings as the quiz */
        private void checkQuestion(Question question) {
            boolean questionTreasureHunt = question.getLocation() != null;
            if (questionTreasureHunt != treasureHunt)
                throw new IllegalArgumentException(
                        "Cannot mix treasure-hunt and non treasure-hunt questions");
        }
    }

    @Override
    public Quiz instantiateLanguage(StringPool pool) {
        String newTitle = pool.get(title);
        List<Question> newQuestions = new ArrayList<>(questions.size());

        for (Question q : questions) newQuestions.add(q.instantiateLanguage(pool));

        return new Quiz(newTitle, newQuestions);
    }
}
