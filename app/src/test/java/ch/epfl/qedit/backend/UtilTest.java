package ch.epfl.qedit.backend;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;

import ch.epfl.qedit.backend.database.Util;
import ch.epfl.qedit.model.answer.AnswerFormat;
import ch.epfl.qedit.model.answer.MatrixFormat;
import ch.epfl.qedit.model.answer.MultiFieldFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;

public class UtilTest {

    private MatrixFormat singleField = new MatrixFormat(1, 1);

    @Test
    public void convertToAnswerFormatTestEmpty() {
        ArrayList<Map<String, Object>> answers = new ArrayList<>();
        assertNull(Util.convertToAnswerFormat(null));
        assertNull(Util.convertToAnswerFormat(answers));
    }

    @Test
    public void convertToAnswerFormatTestFail() {
        ArrayList<Map<String, Object>> answers = new ArrayList<>();
        answers.add(new HashMap<String, Object>());
        answers.add(new HashMap<String, Object>());
        assertNull(Util.convertToAnswerFormat(answers));
    }

    @Test
    public void convertToAnswerFormatTestMatrix() {
        ArrayList<Map<String, Object>> answers = new ArrayList<>();
        HashMap<String, Object> answer = new HashMap<String, Object>();
        answer.put("matrix", null);
        answers.add(answer);
        assertEquals(singleField, Util.convertToAnswerFormat(answers));
    }

    @Test
    public void convertToAnswerFormatTestMultiField() {
        ArrayList<Map<String, Object>> answers = new ArrayList<>();
        HashMap<String, Object> answer = new HashMap<String, Object>();
        answer.put("matrix", null);
        answers.add(answer);
        answers.add(answer);
        assertEquals(
                new MultiFieldFormat(singleField, singleField),
                Util.convertToAnswerFormat(answers));
    }

    @Test
    public void castTest() {
        Object object = Arrays.asList(singleField, singleField, singleField);
        List<AnswerFormat> list = Util.cast(object);
        assertEquals(3, list.size());
    }

    @Test
    public void castTest1() {
        Object object = Arrays.asList("0", "1");
        final List<AnswerFormat> list = Util.cast(object);
        assertThrows(
                ClassCastException.class,
                new ThrowingRunnable() {
                    @Override
                    public void run() {
                        list.get(0).getText();
                    }
                });
    }
}
