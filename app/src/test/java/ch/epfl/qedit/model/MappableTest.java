package ch.epfl.qedit.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import ch.epfl.qedit.model.answer.MatrixFormat;
import ch.epfl.qedit.model.answer.MatrixModel;
import java.util.List;
import java.util.Map;
import org.junit.Test;

public class MappableTest {

    private Question createQuestion(boolean treasureHunt) {
        MatrixFormat matrix = MatrixFormat.singleField(MatrixFormat.Field.textField("???"));
        MatrixModel solutions = new MatrixModel(1, 1);
        matrix.setCorrectAnswer(solutions);
        solutions.updateAnswer(0, 0, "yes");
        return treasureHunt
                ? new Question("title", "text?", matrix, 1, 2, 3)
                : new Question("title", "text?", matrix);
    }

    @Test
    public void testQuestionToMap() {
        Question question = createQuestion(false);
        Map<String, Object> doc = question.toMap();
        assertEquals("title", doc.get(Question.TO_MAP_TITLE));
        assertEquals("text?", doc.get(Question.TO_MAP_TEXT));
        assertNull(question.toMap().get(Question.TO_MAP_RADIUS));
        assertNull(question.toMap().get(Question.TO_MAP_LATITUDE));
        assertNull(question.toMap().get(Question.TO_MAP_LONGITUDE));
    }

    @Test
    public void testQuestionToMap2() {
        Question question = createQuestion(true);
        Map<String, Object> doc = question.toMap();
        assertEquals(3.0, doc.get(Question.TO_MAP_RADIUS));
        assertEquals(2.0, doc.get(Question.TO_MAP_LATITUDE));
        assertEquals(1.0, doc.get(Question.TO_MAP_LONGITUDE));
    }

    @Test
    public void testQuestionToMap3() {
        MatrixFormat matrix = MatrixFormat.singleField(MatrixFormat.Field.textField("???"));
        MatrixModel solutions = new MatrixModel(1, 1);
        matrix.setCorrectAnswer(solutions);
        solutions.updateAnswer(0, 0, "yes");
        Question question = new Question("title", "text?", matrix, 1, 2, 3);
        Map<String, Object> doc = question.toMap();

        Object answers = doc.get(Question.TO_MAP_ANSWERS);
        assertTrue(answers instanceof List);
        Map<String, Object> matrixDoc = ((List<Map<String, Object>>) answers).get(0);
        testMatrixToMap(matrixDoc, 1, 1);
        Object solution = matrixDoc.get(MatrixModel.TO_MAP_DATA);
        assertNotNull(solution);
        assertTrue(solution instanceof Map);
        testMatrixSolution((Map<String, String>) solution, solutions, 1, 1);
        Object fields = matrixDoc.get(MatrixFormat.TO_MAP_FIELDS);
        assertNotNull(solution);
        assertTrue(fields instanceof Map);
        testMatrixFields((Map<String, Map<String, Object>>) fields, matrix);
    }

    private void testMatrixToMap(Map<String, Object> doc, long rows, long cols) {
        assertEquals(rows, doc.get(MatrixFormat.TO_MAP_NUM_ROWS));
        assertEquals(cols, doc.get(MatrixFormat.TO_MAP_NUM_COLUMNS));
        assertEquals(MatrixFormat.TYPE, doc.get(MatrixFormat.TO_MAP_TYPE));
        assertEquals(rows, doc.get(MatrixModel.TO_MAP_NUM_ROWS));
        assertEquals(cols, doc.get(MatrixModel.TO_MAP_NUM_COLUMNS));
    }

    private void testMatrixSolution(
            Map<String, String> solution, MatrixModel model, int rows, int cols) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                String id = i + "," + j;
                assertEquals(model.getAnswer(i, j), solution.get(id));
            }
        }
    }

    private void testMatrixFields(Map<String, Map<String, Object>> fields, MatrixFormat matrix) {
        for (int i = 0; i < matrix.getNumRows(); i++) {
            for (int j = 0; j < matrix.getNumColumns(); j++) {
                String id = i + "," + j;
                Map<String, Object> field = fields.get(id);
                assertNotNull(field);
                testFieldToMap(field, matrix.getField(i, j));
            }
        }
    }

    private void testFieldToMap(Map<String, Object> doc, MatrixFormat.Field field) {
        assertEquals(field.getType().name(), doc.get(MatrixFormat.Field.TO_MAP_TYPE));
        assertEquals(field.getText(), doc.get(MatrixFormat.Field.TO_MAP_TEXT));
    }
}
