package ch.epfl.qedit.model;

public class MatrixModel extends AnswerModel {
    private String[][] matrix;

    public MatrixModel(int numberOfColumns, int numberOfRows) {
        matrix = new String[numberOfColumns][numberOfRows];
    }
}
