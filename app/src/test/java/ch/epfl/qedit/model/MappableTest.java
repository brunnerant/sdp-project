package ch.epfl.qedit.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import ch.epfl.qedit.model.answer.MatrixFormat;
import ch.epfl.qedit.model.answer.MatrixModel;
import java.util.Map;
import org.junit.Test;

public class MappableTest {

    @Test
    public void testQuestionToMap() {
        MatrixFormat matrix = MatrixFormat.singleField(MatrixFormat.Field.textField("???"));
        MatrixModel solutions = new MatrixModel(1, 1);
        matrix.setCorrectAnswer(solutions);
        solutions.updateAnswer(0, 0, "yes");
        Question question = new Question("title", "text?", matrix);
        Map<String, Object> doc = question.toMap();
        assertEquals("title", doc.get(Question.TO_MAP_TITLE));
        assertEquals("text?", doc.get(Question.TO_MAP_TEXT));
    }

    @Test
    public void testQuestionToMap2() {
        MatrixFormat matrix = MatrixFormat.singleField(MatrixFormat.Field.textField("???"));
        MatrixModel solutions = new MatrixModel(1, 1);
        matrix.setCorrectAnswer(solutions);
        solutions.updateAnswer(0, 0, "yes");
        Question question = new Question("title", "text?", matrix);
        assertNull(question.toMap().get(Question.TO_MAP_RADIUS));
        assertNull(question.toMap().get(Question.TO_MAP_LOCATION));
    }
}
