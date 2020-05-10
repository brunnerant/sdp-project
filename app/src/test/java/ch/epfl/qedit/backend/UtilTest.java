package ch.epfl.qedit.backend;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.epfl.qedit.backend.database.Util;
import ch.epfl.qedit.model.answer.AnswerFormat;
import ch.epfl.qedit.model.answer.MatrixFormat;
import ch.epfl.qedit.model.answer.MultiFieldFormat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class UtilTest {
    private List<Object> answerFormats;
    private MultiFieldFormat expectedAnswerFormats;

    private Map<String, Object> answerFormat;
    private AnswerFormat expectedAnswerFormat;

    private String[] fieldStrings =
            new String[] {
                "pre_filled", "text", "unsigned_int", "signed_int", "unsigned_float", "signed_float"
            };

    private MatrixFormat.Field.Type[] fieldTypes =
            new MatrixFormat.Field.Type[] {
                MatrixFormat.Field.Type.PreFilled, MatrixFormat.Field.Type.Text,
                MatrixFormat.Field.Type.UnsignedInt, MatrixFormat.Field.Type.SignedInt,
                MatrixFormat.Field.Type.UnsignedFloat, MatrixFormat.Field.Type.SignedFloat
            };

    @Before
    public void init() {
        createAnswerFormats();
        createExpectedAnswerFormats();
    }

    public void createAnswerFormats() {
        answerFormat = new HashMap<>();
        answerFormat.put("type", "matrix");
        answerFormat.put("rows", 2);
        answerFormat.put("columns", 3);

        Map<String, Object> matrix = new HashMap<>();
        for (int i = 0; i < 6; i++) {
            Map<String, Object> field = new HashMap<>();
            field.put("type", fieldStrings[i]);
            field.put("text", "text" + i);
            field.put("max_characters", i - 1);
            matrix.put((i / 3) + "," + (i % 3), field);
        }

        answerFormat.put("matrix", matrix);

        answerFormats = new ArrayList<>();
        answerFormats.add(answerFormat);
        answerFormats.add(answerFormat);
    }

    public void createExpectedAnswerFormats() {
        expectedAnswerFormat =
                new MatrixFormat.Builder(2, 3)
                        .withField(0, 0, MatrixFormat.Field.preFilledField("text0"))
                        .withField(0, 1, MatrixFormat.Field.textField("text1", 0))
                        .withField(0, 2, MatrixFormat.Field.numericField(false, false, "text2", 1))
                        .withField(1, 0, MatrixFormat.Field.numericField(false, true, "text3", 2))
                        .withField(1, 1, MatrixFormat.Field.numericField(true, false, "text4", 3))
                        .withField(1, 2, MatrixFormat.Field.numericField(true, true, "text5", 4))
                        .build();

        expectedAnswerFormats = new MultiFieldFormat(Collections.nCopies(2, expectedAnswerFormat));
    }

    @Test
    public void assertAnswerFormatsCanBeExtracted() throws Util.FormatException {
        AnswerFormat actualAnswerFormats = Util.extractAnswerFormats(answerFormats);
        AnswerFormat actualAnswerFormat = Util.extractAnswerFormat(answerFormat);

        assertEquals(expectedAnswerFormats, actualAnswerFormats);
        assertEquals(expectedAnswerFormat, actualAnswerFormat);
        assertEquals(
                expectedAnswerFormat,
                Util.extractAnswerFormats(Collections.singletonList(answerFormat)));
    }

    @Test
    public void testExtractInvalidAnswerFormats() {
        assertThrows(
                Util.FormatException.class,
                () -> Util.extractAnswerFormats(Collections.emptyList()));
    }

    private Map<String, Object> answerFormatWithout(String missing) {
        Map<String, Object> format = new HashMap<>();
        format.put("type", "matrix");
        format.put("rows", 1);
        format.put("columns", 1);
        format.put("matrix", new HashMap<>());
        format.remove(missing);
        return format;
    }

    private void testExtractAnswerFormat(Map<String, Object> format) {
        assertThrows(Util.FormatException.class, () -> Util.extractAnswerFormat(format));
    }

    @Test
    public void testExtractInvalidAnswerFormat() {
        Map<String, Object> format = answerFormatWithout("type");
        testExtractAnswerFormat(format);
        format.put("type", "invalid");
        testExtractAnswerFormat(format);
    }

    private void testExtractMatrixWithout(String missing) {
        assertThrows(
                Util.FormatException.class,
                () -> Util.extractMatrixFormat(answerFormatWithout(missing)));
    }

    @Test
    public void testExtractInvalidMatrixFormat() {
        testExtractMatrixWithout("rows");
        testExtractMatrixWithout("columns");
        testExtractMatrixWithout("matrix");
    }

    public void testFieldIndexThrows(String index, int rows, int columns) {
        assertThrows(
                Util.FormatException.class, () -> Util.extractFieldIndex(index, rows, columns));
    }

    @Test
    public void testExtractInvalidFieldIndex() {
        testFieldIndexThrows("abc", 100, 100);
        testFieldIndexThrows("0,", 100, 100);
        testFieldIndexThrows(",0", 100, 100);
        testFieldIndexThrows("-1,0", 100, 100);
        testFieldIndexThrows("0,-1", 100, 100);
        testFieldIndexThrows("100,0", 100, 100);
        testFieldIndexThrows("0,100", 100, 100);
    }

    @Test
    public void testInvalidFieldType() {
        assertThrows(Util.FormatException.class, () -> Util.extractFieldType("illegal"));
    }

    private Map<String, Object> fieldWithout(String missing) {
        Map<String, Object> field = new HashMap<>();
        field.put("type", "");
        field.put("text", "");
        field.put("max_characters", 0);
        field.remove(missing);
        return field;
    }

    private void testExtractFieldWithout(String missing) {
        assertThrows(Util.FormatException.class, () -> Util.extractField(fieldWithout(missing)));
    }

    @Test
    public void testInvalidField() {
        testExtractFieldWithout("type");
        testExtractFieldWithout("text");
        testExtractFieldWithout("max_characters");
    }
}
