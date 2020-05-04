package ch.epfl.qedit.model.answer;

import ch.epfl.qedit.model.StringPool;
import ch.epfl.qedit.view.answer.AnswerFragment;
import ch.epfl.qedit.view.answer.MatrixFragment;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class represents matrices where the user can enter his answers. Fields of the matrices can
 * be used to enter numbers (signed and unsigned, integer or decimal), text, or can be pre-filled
 * with some text.
 */
public final class MatrixFormat extends AnswerFormat {
    /**
     * This class represents a field of a matrix. It can be pre-filled with text (meaning the user
     * cannot enter something in it), it can be a text input, or a number input.
     */
    public static class Field implements Serializable {
        /** This is used to represent unlimited length for fields */
        public static final int NO_LIMIT = -1;

        /** Those are the types of fields of a matrix format */
        public enum Type {
            PreFilled,
            Text,
            UnsignedInt,
            SignedInt,
            UnsignedFloat,
            SignedFloat;

            /** Returns true iff minus sign is allowed in this field type */
            public boolean isSigned() {
                switch (this) {
                    case SignedInt:
                    case SignedFloat:
                        return true;
                    default:
                        return false;
                }
            }

            /** Returns true iff decimal dot is allowed in this field type */
            public boolean isDecimal() {
                switch (this) {
                    case SignedFloat:
                    case UnsignedFloat:
                        return true;
                    default:
                        return false;
                }
            }
        }

        private Type type;
        private int maxCharacters;

        // This is the text of a pre-filled field, or the hint for the other types
        private String text;

        /**
         * Constructs a field with the given characteristics. Note that this constructor is exposed
         * for the backend, so you should preferably call the static factory methods if you can.
         */
        public Field(Type type, int maxCharacters, String text) {
            this.type = type;
            this.maxCharacters = maxCharacters;
            this.text = text;
        }

        /** Returns a field pre-filled with the given text */
        public static Field preFilledField(String text) {
            return new Field(Type.PreFilled, NO_LIMIT, text);
        }

        /** Returns a text field with the given hint and maximum characters */
        public static Field textField(String hint, int maxCharacters) {
            return new Field(Type.Text, maxCharacters, hint);
        }

        /** Returns a numeric field with the giben characteristics */
        public static Field numericField(
                boolean decimal, boolean signed, String hint, int maxCharacters) {
            Type type;

            if (decimal) {
                if (signed) type = Type.SignedFloat;
                else type = Type.UnsignedFloat;
            } else {
                if (signed) type = Type.SignedInt;
                else type = Type.UnsignedInt;
            }

            return new Field(type, maxCharacters, hint);
        }

        public Type getType() {
            return type;
        }

        public int getMaxCharacters() {
            return maxCharacters;
        }

        public String getText() {
            return text;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof Field) {
                Field that = (Field) o;
                return maxCharacters == that.maxCharacters
                        && type == that.type
                        && text.equals(that.text);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(type, maxCharacters, text);
        }
    }

    /**
     * This class allows to build matrix formats field by field. If the matrix format to be built
     * consists of the same fields, use the helper method `uniform` instead.
     */
    public static class Builder {
        private int numRows;
        private int numColumns;
        private List<List<Field>> fields;

        /**
         * Creates a builder for a matrix format. By default, all the fields are empty and cannot be
         * edited.
         *
         * @throws IllegalArgumentException if the number of rows or columns is less than 1
         */
        public Builder(int numRows, int numColumns) {
            if (numRows < 1 || numColumns < 1) throw new IllegalArgumentException();

            this.numRows = numRows;
            this.numColumns = numColumns;

            // By default, all the cells are empty and cannot be edited
            Field field = Field.preFilledField("");
            this.fields = new ArrayList<>(numRows);
            for (int i = 0; i < numRows; i++) {
                fields.add(new ArrayList<Field>(numColumns));
                for (int j = 0; j < numColumns; j++) fields.get(i).add(field);
            }
        }

        /**
         * Builds the matrix format that was incrementally constructed. Note that this invalidates
         * the builder.
         *
         * @throws IllegalStateException if the method was already called before
         */
        public MatrixFormat build() {
            if (fields == null) throw new IllegalStateException();

            MatrixFormat result = new MatrixFormat(null, numRows, numColumns, fields);
            fields = null;
            return result;
        }

        /** Adds the given field at the given position in the matrix. */
        public Builder withField(int row, int col, Field field) {
            fields.get(row).set(col, field);
            return this;
        }
    }

    private int numRows;
    private int numColumns;
    private List<List<Field>> fields;

    // This constructor is private because the builder or the static factory methods should
    // be used instead. This guarantees that this constructor will be called with valid
    // arguments.
    private MatrixFormat(String text, int numRows, int numColumns, List<List<Field>> fields) {
        super(text);
        this.numRows = numRows;
        this.numColumns = numColumns;
        this.fields = fields;
    }

    /** Returns a matrix format that is uniformly filled with the same field. */
    public static MatrixFormat uniform(int numRows, int numColumns, Field field) {
        Builder builder = new Builder(numRows, numColumns);

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numColumns; j++) builder.withField(i, j, field);
        }

        return builder.build();
    }

    /** Returns a single-field matrix format */
    public static MatrixFormat singleField(Field field) {
        return uniform(1, 1, field);
    }

    public int getNumRows() {
        return numRows;
    }

    public int getNumColumns() {
        return numColumns;
    }

    public Field getField(int row, int col) {
        return fields.get(row).get(col);
    }

    @Override
    public AnswerModel getEmptyAnswerModel() {
        return new MatrixModel(numRows, numColumns);
    }

    @Override
    public AnswerFragment<MatrixFormat, MatrixModel> getAnswerFragment() {
        return new MatrixFragment();
    }

    static MatrixFormat parse(String format, String text) {
        /** Match format: 'matrixNxM' where N and M are [0-9]+ */
        if (Pattern.compile("^(\\s*)matrix(\\d+)x(\\d+)(\\s*)$").matcher(format).find()) {
            /** Extract the row and column size */
            Matcher number = Pattern.compile("(\\d+)").matcher(format);
            number.find();
            int numRows = Integer.parseInt(number.group(1));
            number.find();
            int numColumns = Integer.parseInt(number.group(1));
            return uniform(numRows, numColumns, Field.textField("", Field.NO_LIMIT));
        } else {
            return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (super.equals(o) && o instanceof MatrixFormat) {
            MatrixFormat that = (MatrixFormat) o;
            return numRows == that.numRows
                    && numColumns == that.numColumns
                    && Objects.equals(fields, that.fields);
        }
        return false;
    }

    @Override
    public AnswerFormat instantiateLanguage(StringPool pool) {
        Builder b = new Builder(numRows, numColumns);

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numColumns; j++) {
                Field f = getField(i, j);
                String newText = pool.get(f.getText());
                Field newField = new Field(f.getType(), f.getMaxCharacters(), newText);
                b.withField(i, j, newField);
            }
        }

        return b.build();
    }
}
