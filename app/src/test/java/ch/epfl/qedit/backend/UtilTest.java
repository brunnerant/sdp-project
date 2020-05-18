package ch.epfl.qedit.backend;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import ch.epfl.qedit.model.answer.AnswerFormat;
import ch.epfl.qedit.model.answer.MatrixFormat;
import ch.epfl.qedit.model.answer.MultiFieldFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;

public class UtilTest {
    private List<Object> answerFormats;
    private MultiFieldFormat expectedAnswerFormats;

    private Map<String, Object> answerFormat;
    private AnswerFormat expectedAnswerFormat;

    private String[] fieldStrings =
            new String[] {
                MatrixFormat.Field.Type.PreFilled.name(),
                MatrixFormat.Field.Type.Text.name(),
                MatrixFormat.Field.Type.UnsignedInt.name(),
                MatrixFormat.Field.Type.SignedInt.name(),
                MatrixFormat.Field.Type.UnsignedFloat.name(),
                MatrixFormat.Field.Type.SignedFloat.name()
            };

    @Before
    public void init() {
        createAnswerFormats();
        createExpectedAnswerFormats();
    }

    public void createAnswerFormats() {
        answerFormat = new HashMap<>();
        answerFormat.put(MatrixFormat.TO_MAP_TYPE, MatrixFormat.TYPE);
        answerFormat.put(MatrixFormat.TO_MAP_NUM_ROWS, 2);
        answerFormat.put(MatrixFormat.TO_MAP_NUM_COLUMNS, 3);

        Map<String, Object> matrix = new HashMap<>();
        for (int i = 0; i < 6; i++) {
            Map<String, Object> field = new HashMap<>();
            field.put(MatrixFormat.Field.TO_MAP_TYPE, fieldStrings[i]);
            field.put(MatrixFormat.Field.TO_MAP_TEXT, "text" + i);
            matrix.put((i / 3) + "," + (i % 3), field);
        }

        answerFormat.put(MatrixFormat.TO_MAP_FIELDS, matrix);

        answerFormats = new ArrayList<>();
        answerFormats.add(answerFormat);
        answerFormats.add(answerFormat);
    }

    public void createExpectedAnswerFormats() {
        expectedAnswerFormat =
                new MatrixFormat.Builder(2, 3)
                        .withField(0, 0, MatrixFormat.Field.preFilledField("text0"))
                        .withField(0, 1, MatrixFormat.Field.textField("text1"))
                        .withField(0, 2, MatrixFormat.Field.numericField(false, false, "text2"))
                        .withField(1, 0, MatrixFormat.Field.numericField(false, true, "text3"))
                        .withField(1, 1, MatrixFormat.Field.numericField(true, false, "text4"))
                        .withField(1, 2, MatrixFormat.Field.numericField(true, true, "text5"))
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
        format.put(MatrixFormat.TO_MAP_TYPE, MatrixFormat.TYPE);
        format.put(MatrixFormat.TO_MAP_NUM_ROWS, 1);
        format.put(MatrixFormat.TO_MAP_NUM_COLUMNS, 1);
        format.put(MatrixFormat.TO_MAP_FIELDS, new HashMap<>());
        format.remove(missing);
        return format;
    }

    private void testExtractAnswerFormat(Map<String, Object> format) {
        assertThrows(Util.FormatException.class, () -> Util.extractAnswerFormat(format));
    }

    @Test
    public void testExtractInvalidAnswerFormat() {
        Map<String, Object> format = answerFormatWithout(AnswerFormat.TO_MAP_TYPE);
        testExtractAnswerFormat(format);
        format.put(AnswerFormat.TO_MAP_TYPE, "invalid");
        testExtractAnswerFormat(format);
    }

    private void testExtractMatrixWithout(String missing) {
        assertThrows(
                Util.FormatException.class,
                () -> Util.extractMatrixFormat(answerFormatWithout(missing)));
    }

    @Test
    public void testExtractInvalidMatrixFormat() {
        testExtractMatrixWithout(MatrixFormat.TO_MAP_NUM_ROWS);
        testExtractMatrixWithout(MatrixFormat.TO_MAP_NUM_COLUMNS);
        testExtractMatrixWithout(MatrixFormat.TO_MAP_FIELDS);
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

    private Map<String, Object> fieldWithout(String missing) {
        Map<String, Object> field = new HashMap<>();
        field.put(MatrixFormat.Field.TO_MAP_TYPE, "");
        field.put(MatrixFormat.Field.TO_MAP_TEXT, "");
        field.remove(missing);
        return field;
    }

    private void testExtractFieldWithout(String missing) {
        assertThrows(Util.FormatException.class, () -> Util.extractField(fieldWithout(missing)));
    }

    @Test
    public void testInvalidField() {
        testExtractFieldWithout(MatrixFormat.Field.TO_MAP_TYPE);
        testExtractFieldWithout(MatrixFormat.Field.TO_MAP_TEXT);
    }
}
