package ch.epfl.qedit.model;

import java.io.Serializable;

/** This class represents all the answer formats that are available in the app. */
public abstract class AnswerFormat implements Serializable {

    /** This method is used to implement the visitor pattern */
    public abstract void accept(Visitor visitor);

    public static class NumberField extends AnswerFormat {
        private final float min;
        private final float max;
        private final int digits;

        public NumberField(float min, float max, int digits) {
            if (max <= min || digits < 0) throw new IllegalArgumentException();

            this.min = min;
            this.max = max;
            this.digits = digits;
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

        @Override
        public void accept(Visitor visitor) {
            visitor.visitNumberField(this);
        }
    }

    public interface Visitor {
        void visitNumberField(NumberField field);
    }
}
