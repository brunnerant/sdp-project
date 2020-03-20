package ch.epfl.qedit.model;

/** This class represents all the answer formats that are available in the app. */
public abstract class AnswerFormat {

    /** This method is used to implement the visitor pattern */
    public abstract void accept(Visitor visitor);

    public static class NumberField extends AnswerFormat {
        private final float min;
        private final float max;
        private final int digits;
        private final int row = 1;
        private final int column = 1;

        public NumberField(float min, float max, int digits, int row, int column) {
            if (max <= min || digits < 0) throw new IllegalArgumentException();

            this.min = min;
            this.max = max;
            this.digits = digits;
        }

        public NumberField(float min, float max, int digits) {
            this(min, max, digits, 1, 1);
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

        public int getRow() {
            return row;
        }

        public int getColumn() {
            return column;
        }

        @Override
        public void accept(Visitor visitor) {
            visitor.visitNumberField(this);
        }
    }

    public interface Visitor {
        void visitNumberField(NumberField field);

        void visitMatrixAnswerFormat(MatrixFormat matrixFormat);
    }
}
