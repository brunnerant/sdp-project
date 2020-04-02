package ch.epfl.qedit.model.answer;

/** This class is used to store answer given by the user via MatrixFormats */
public class MatrixModel extends AnswerModel {
    private String[][] matrix;

    public MatrixModel(int numberOfColumns, int numberOfRows) {
        matrix = new String[numberOfRows][numberOfColumns];

        for (String[] row : matrix) {
            for (String elem : row) {
                elem = "";
            }
        }
    }

    public void updateAnswer(int row, int col, String newValue) {
        if (row < matrix.length && col < matrix[row].length) {
            matrix[row][col] = newValue;
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    public String getAnswer(int row, int col) {
        if (row < matrix.length && col < matrix[row].length) {
            return matrix[row][col];
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitMatrixModel(this);
    }
}
