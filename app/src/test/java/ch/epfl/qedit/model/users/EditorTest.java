package ch.epfl.qedit.model.users;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import ch.epfl.qedit.model.Question;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class EditorTest {
    @Test
    public void EditorConstructorTest() {
        Editor editor = new Editor("Pierre", "Paul", 28, "fr");
        assertEquals(editor.getFirstName(), "Pierre");
        assertEquals(editor.getLanguage(), "fr");
        assertEquals(editor.getLastName(), "Paul");
        assertEquals(editor.getUserId(), 28);
    }

    @Test
    public void EditorCreateQuizTest() {
        Editor editor = new Editor("Pierre", "Paul", 28, "fr");
        List<Question> questions = new ArrayList<>();
        assertNotNull(editor.createQuiz(questions));
    }
}
