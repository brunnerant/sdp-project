package ch.epfl.qedit.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import ch.epfl.qedit.model.answer.MatrixFormat;
import ch.epfl.qedit.model.answer.MatrixModel;
import java.util.Map;
import org.junit.Test;

public class MappableTest {

    private Question createQuestion() {
        MatrixFormat matrix = MatrixFormat.singleField(MatrixFormat.Field.textField("???"));
        MatrixModel solutions = new MatrixModel(1, 1);
        matrix.setCorrectAnswer(solutions);
        solutions.updateAnswer(0, 0, "yes");
        return new Question("title", "text?", matrix);
    }

    @Test
    public void testQuestionToMap() {
        Question question = createQuestion();
        Map<String, Object> doc = question.toMap();
        assertEquals("title", doc.get(Question.TO_MAP_TITLE));
        assertEquals("text?", doc.get(Question.TO_MAP_TEXT));
        assertNull(question.toMap().get(Question.TO_MAP_RADIUS));
        assertNull(question.toMap().get(Question.TO_MAP_LATITUDE));
        assertNull(question.toMap().get(Question.TO_MAP_LONGITUDE));
    }

    @Test
    public void testQuestionToMap3() {
        MatrixFormat matrix = MatrixFormat.singleField(MatrixFormat.Field.textField("???"));
        MatrixModel solutions = new MatrixModel(1, 1);
        matrix.setCorrectAnswer(solutions);
        solutions.updateAnswer(0, 0, "yes");
        Question question = new Question("title", "text?", matrix, 1, 2, 3);
        assertEquals(3.0, question.toMap().get(Question.TO_MAP_RADIUS));
        assertEquals(2.0, question.toMap().get(Question.TO_MAP_LATITUDE));
        assertEquals(1.0, question.toMap().get(Question.TO_MAP_LONGITUDE));
    }

    private void testMatrixToMap(MatrixFormat matrix) {
        Map<String, Object> doc = matrix.toMap();
        assertEquals(matrix.getNumRows(), doc.get(MatrixFormat.TO_MAP_NUM_ROWS));
        assertEquals(matrix.getNumColumns(), doc.get(MatrixFormat.TO_MAP_NUM_COLUMNS));
    }
}
