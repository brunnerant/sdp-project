package ch.epfl.qedit.model;

/** This class represents all the answer formats that are available in the app. */
public abstract class AnswerFormat {

    /** This method is used to implement the visitor pattern */
    public abstract void accept(Visitor visitor);

    public static class NumberField<T extends Number> extends AnswerFormat {
        private final T min;
        private final T max;

        public NumberField(T min, T max) {
            this.min = min;
            this.max = max;
        }

        public T getMin() {
            return min;
        }

        public T getMax() {
            return max;
        }

        @Override
        public void accept(Visitor visitor) {
            visitor.visitNumberField(this);
        }
    }

    public interface Visitor {
        public <T extends Number> void visitNumberField(NumberField<T> field);
    }
}
