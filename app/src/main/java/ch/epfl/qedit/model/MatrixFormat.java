package ch.epfl.qedit.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MatrixFormat extends AnswerFormat {

    private boolean hasDecimal = true;
    private boolean hasSign = true;

    private int tableRowsNumber = 1;
    private int tableColumnsNumber = 1;
    private int maxCharacters = 5;
    private String hintString;

    public MatrixFormat(int tableColumnsNumber, int tableRowsNumber) {
        super();
        this.tableRowsNumber = tableRowsNumber;
        this.tableColumnsNumber = tableColumnsNumber;
        this.hintString = hint();
    }

    private MatrixFormat(
            int tableColumnsNumber,
            int tableRowsNumber,
            boolean hasDecimal,
            boolean hasSign,
            int maxCharacters) {
        this.tableColumnsNumber = tableColumnsNumber;
        this.tableRowsNumber = tableRowsNumber;
        this.hasDecimal = hasDecimal;
        this.hasSign = hasSign;
        this.maxCharacters = maxCharacters;
        this.hintString = hint();
    }

    public static MatrixFormat parse(String format) {
        /** Match format: 'matrixNxM' where N and M are [0-9]+ */
        if (Pattern.compile("^(\\s*)matrix(\\d+)x(\\d+)(\\s*)$").matcher(format).find()) {
            /** Extract the row and column size */
            Matcher number = Pattern.compile("(\\d+)").matcher(format);
            number.find();
            int i = Integer.parseInt(number.group(1));
            number.find();
            int j = Integer.parseInt(number.group(1));
            return new MatrixFormat(i, j);
        } else {
            return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof MatrixFormat) {
            MatrixFormat other = (MatrixFormat) o;
            return this.hasDecimal == other.hasDecimal
                    && this.hasSign == other.hasSign
                    && this.tableRowsNumber == other.tableRowsNumber
                    && this.tableColumnsNumber == other.tableColumnsNumber
                    && this.maxCharacters == other.maxCharacters
                    && this.hintString.equals(other.hintString);
            // && this.id.equals(other.id);
        }
        return false;
    }

    public static MatrixFormat createMatrix3x3() {
        return new MatrixFormat(3, 3, true, true, 5);
    }

    public static MatrixFormat createMatrix3x3(
            boolean hasDecimal, boolean hasSign, int maxCharacters) {
        return new MatrixFormat(3, 3, hasDecimal, hasSign, maxCharacters);
    }

    public static MatrixFormat createSingleField(
            boolean hasDecimal, boolean hasSign, int maxCharacters) {
        return new MatrixFormat(1, 1, hasDecimal, hasSign, maxCharacters);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitMatrixAnswerFormat(this);
    }

    public int getTableRowsNumber() {
        return tableRowsNumber;
    }

    public int getTableColumnsNumber() {
        return tableColumnsNumber;
    }

    public boolean hasDecimal() {
        return hasDecimal;
    }

    public boolean hasSign() {
        return hasSign;
    }

    public int getMaxCharacters() {
        return maxCharacters;
    }

    public void setHint(String hint) {
        this.hintString = hint;
    }

    public String getHint() {
        return hintString;
    }

    // Function that allows to be placed as a placeholder for the EditText
    private String hint() {
        return String.format("%0" + maxCharacters + "d", 0);
    }
}
