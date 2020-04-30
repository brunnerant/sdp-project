package ch.epfl.qedit.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;

import ch.epfl.qedit.model.answer.AnswerFormat;
import ch.epfl.qedit.model.answer.MatrixFormat;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;

public class QuizTest {

    private AnswerFormat answerFormat =
            MatrixFormat.singleField(MatrixFormat.Field.preFilledField(""));

    private List<Question> initQuestionList() {
        List<Question> questions = new ArrayList<>();
        questions.add(new Question("q1", "text", answerFormat));
        questions.add(new Question("q2", "text", answerFormat));
        questions.add(new Question("q3", "text", answerFormat));

        return questions;
    }

    @Test
    public void quizConstructorTest() {
        Quiz quiz = new Quiz("Title", initQuestionList());
        assertNotNull(quiz);
        assertEquals(3, quiz.getQuestions().size());
        assertEquals("q1", quiz.getQuestions().get(0).getTitle());
        assertEquals("text", quiz.getQuestions().get(1).getText());
    }

    @Test
    public void getQuestionIsImmutableTest() {
        Quiz quiz = new Quiz("Title", initQuestionList());
        final List<Question> questions = quiz.getQuestions();
        assertThrows(
                UnsupportedOperationException.class,
                new ThrowingRunnable() {
                    @Override
                    public void run() {
                        questions.add(null);
                    }
                });
    }

    @Test
    public void getTitleTest() {
        Quiz quiz = new Quiz("About Math and Spider", initQuestionList());
        assertEquals(quiz.getTitle(), "About Math and Spider");
    }

    // BUILDER TESTS //

    @Test
    public void builderTest() {
        List<Question> questions = initQuestionList();
        Quiz q0 = new Quiz("title", questions);
        Quiz.Builder builder = new Quiz.Builder();
        builder.add(questions.get(0)).add(questions.get(1)).add(questions.get(2));
        assertEquals(q0.getQuestions(), builder.build().getQuestions());
    }

    @Test
    public void builderAdd() {
        List<Question> questions = initQuestionList();
        Quiz q0 = new Quiz("title", questions);
        Quiz.Builder builder = new Quiz.Builder();
        builder.add(questions.get(1)).add(0, questions.get(0)).add(questions.get(2));
        assertEquals(q0.getQuestions(), builder.build().getQuestions());
    }

    @Test
    public void builderSwap() {
        List<Question> questions = initQuestionList();
        Quiz q0 = new Quiz("title", questions);
        Quiz.Builder builder = new Quiz.Builder();
        builder.add(questions.get(2)).add(questions.get(1)).add(questions.get(0)).swap(0, 2);
        assertEquals(q0.getQuestions(), builder.build().getQuestions());
    }

    @Test
    public void builderRemove() {
        List<Question> questions = initQuestionList();
        Quiz q0 = new Quiz("title", questions);
        Quiz.Builder builder = new Quiz.Builder();
        builder.add(questions.get(0))
                .add(questions.get(0))
                .add(questions.get(1))
                .add(questions.get(2))
                .remove(1);
        assertEquals(q0.getQuestions(), builder.build().getQuestions());
    }

    @Test(expected = IllegalStateException.class)
    public void builderFail1Test() {
        Quiz.Builder builder = new Quiz.Builder();
        builder.build();
        builder.build();
    }

    @Test(expected = IllegalStateException.class)
    public void builderAddFail1Test() {
        Quiz.Builder builder = new Quiz.Builder();
        builder.build();
        builder.add(null);
    }

    @Test(expected = IllegalStateException.class)
    public void builderAddFail2Test() {
        Quiz.Builder builder = new Quiz.Builder();
        builder.build();
        builder.add(0, null);
    }

    @Test(expected = IllegalStateException.class)
    public void builderSwapFailTest() {
        Quiz.Builder builder = new Quiz.Builder();
        builder.build();
        builder.swap(0, 5);
    }

    @Test(expected = IllegalStateException.class)
    public void builderSizeFailTest() {
        Quiz.Builder builder = new Quiz.Builder();
        builder.build();
        builder.size();
    }

    @Test(expected = IllegalStateException.class)
    public void builderRemoveFailTest() {
        Quiz.Builder builder = new Quiz.Builder();
        builder.build();
        builder.remove(0);
    }
}
