package ch.epfl.qedit.model;

/** This class represents all the answer formats that are available in the app. */
public abstract class AnswerFormat {

    /** This method is used to implement the visitor pattern */
    public abstract void accept(Visitor visitor);

    public static class IntegerField extends AnswerFormat {
        private final int min;
        private final int max;

        public IntegerField(int min, int max) {
            this.min = min;
            this.max = max;
        }

        public int getMin() {
            return min;
        }

        public int getMax() {
            return max;
        }

        @Override
        public void accept(Visitor visitor) {
            visitor.visitIntegerField(this);
        }
    }

    public static class FloatField extends AnswerFormat {
        private final float min;
        private final float max;

        public FloatField(float min, float max) {
            this.min = min;
            this.max = max;
        }

        public float getMin() {
            return min;
        }

        public float getMax() {
            return max;
        }

        @Override
        public void accept(Visitor visitor) {
            visitor.visitFloatField(this);
        }
    }

    public interface Visitor {
        public void visitIntegerField(IntegerField field);

        public void visitFloatField(FloatField field);
    }
}
