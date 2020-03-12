package ch.epfl.qedit.model;

/** This class represents all the answer formats that are available in the app. */
public abstract class AnswerFormat {
    public enum QuestionsTypes {
        SIMPLE,
        MORE_ONE_QUESTION,
        OTHER
    }
    /** This method is used to implement the visitor pattern */
    public abstract void accept(Visitor visitor);

    public static class NumberField extends AnswerFormat {
        private final float min;
        private final float max;
        private final int digits;

        private int numberOfAnswers = 0;
        private float[] acceptedAnswers;

        private QuestionsTypes questionType;

        public NumberField(float min, float max, int digits) {
            if (max <= min || digits < 0) throw new IllegalArgumentException();

            this.min = min;
            this.max = max;
            this.digits = digits;
        }

        public NumberField(float min, float max, int digits, int numberOfAnswers) {
            this(min, max, digits);
            this.numberOfAnswers = numberOfAnswers;
        }

        public QuestionsTypes getType() {
            return questionType;
        }

        public void setAcceptedAnswers(float[] acceptedAnswers) {
            this.acceptedAnswers = acceptedAnswers;
        }

        public boolean isAnswer(float answer) {
            for (int i = 0; i < acceptedAnswers.length; ++i) {
                if (acceptedAnswers[i] == answer) {
                    return true;
                }
            }

            return false;
        }

        public float getMin() {
            return min;
        }

        public float getMax() {
            return max;
        }

        public int getDigits() {
            return digits;
        }

        public int getNumberOfAnswers() {
            return numberOfAnswers;
        }

        @Override
        public void accept(Visitor visitor) {
            visitor.visitNumberField(this);
        }
    }

    public interface Visitor {
        void visitNumberField(NumberField field);
    }
}
