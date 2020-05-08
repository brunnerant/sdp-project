package ch.epfl.qedit.model.answer;

import androidx.annotation.Nullable;

/** This class is used to store answer given by the user via MatrixFormats */
public class MatrixModel extends AnswerModel {
    private final String[][] matrix;
    private int numRows;
    private int numCols;

    public MatrixModel(int numRows, int numColumns) {
        this.numRows = numRows;
        this.numCols = numColumns;
        matrix = new String[numRows][numColumns];

        for (int i = 0; i < numRows; ++i) {
            for (int j = 0; j < numColumns; ++j) {
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

    @Override
    public boolean equals(@Nullable Object o) {
        Boolean toRet = true;
        if (!(o instanceof MatrixModel)) return false;

        for (int i = 0; i < numRows; ++i) {
            for (int j = 0; j < numCols; ++j) {
                toRet &= checkFieldEquals(matrix[i][j], (MatrixModel) o, i, j);
            }
        }

        return toRet;
    }

    private boolean checkFieldEquals(
            String matrixField, MatrixModel objectMatrixModel, int row, int col) {

        try {
            return matrixField.trim().toLowerCase()
                    == objectMatrixModel.getAnswer(row, col).trim().toLowerCase();
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
    }
}
