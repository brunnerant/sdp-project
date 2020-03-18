package ch.epfl.qedit.model;

public class MatrixFormat extends AnswerFormat {
    private boolean hasDecimal = true;
    private boolean hasSign = true;

    private int tableRowsNumber = 1;
    private int tableColumnsNumber = 1;
    private int maxCharacters = 3;
    private String hintString;

    public MatrixFormat(
            int tableColumnsNumber,
            int tableRowsNumber,
            boolean hasDecimal,
            boolean hasSign,
            int maxCharacters) {
        super();
        this.tableColumnsNumber = tableColumnsNumber;
        this.tableRowsNumber = tableRowsNumber;
        this.hasDecimal = hasDecimal;
        this.hasSign = hasSign;
        this.maxCharacters = maxCharacters;
        this.hintString = hint();
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
        return String.format("%0" + maxCharacters + "d", 0).replace("0", "0");
    }
}
