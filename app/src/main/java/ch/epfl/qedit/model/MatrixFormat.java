package ch.epfl.qedit.model;

public class MatrixFormat extends AnswerFormat {
    private boolean hasDecimal = true;
    private boolean hasSign = true;

    private int tableRowsNumber = 1;
    private int tableColumnsNumber = 1;
    private int maxCharacters = 5;
    private String hintString;
    private String id = "m0";

    public MatrixFormat(int tableColumnsNumber, int tableRowsNumber) {
        super();
        this.tableRowsNumber = tableRowsNumber;
        this.tableColumnsNumber = tableColumnsNumber;
        this.hintString = hint();
    }

    public MatrixFormat(
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

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    // Function that allows to be placed as a placeholder for the EditText
    private String hint() {
        return String.format("%0" + maxCharacters + "d", 0).replace("0", "0");
    }
}
