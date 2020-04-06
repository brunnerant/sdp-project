package ch.epfl.qedit.model.answer;

/** This class is used to store answer given by the user via MatrixFormats */
public class MatrixModel extends AnswerModel {
    private final String[][] matrix;

    public MatrixModel(int numberOfColumns, int numberOfRows) {
        matrix = new String[numberOfRows][numberOfColumns];

        for (int i = 0; i < numberOfRows; ++i) {
            for (int j = 0; j < numberOfColumns; ++j) {
                matrix[i][j] = "";
            }
        }
    }

    public void updateAnswer(int row, int col, String newValue) throws IndexOutOfBoundsException {
        if (0 <= row && row < matrix.length && 0 <= col && col < matrix[row].length) {
            matrix[row][col] = newValue;
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    public String getAnswer(int row, int col) throws IndexOutOfBoundsException {
        if (0 <= row && row < matrix.length && 0 <= col && col < matrix[row].length) {
            return matrix[row][col];
        } else {
            throw new IndexOutOfBoundsException();
        }
    }
}
