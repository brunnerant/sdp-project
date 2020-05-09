package ch.epfl.qedit.model;

import android.location.Location;

import org.junit.Test;
import org.junit.function.ThrowingRunnable;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.qedit.model.answer.AnswerFormat;
import ch.epfl.qedit.model.answer.MatrixFormat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;

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
        builder.append(questions.get(0)).append(questions.get(1)).append(questions.get(2));
        assertEquals(q0.getQuestions(), builder.build().getQuestions());
    }

    @Test
    public void builderAdd() {
        List<Question> questions = initQuestionList();
        Quiz q0 = new Quiz("title", questions);
        Quiz.Builder builder = new Quiz.Builder();
        builder.append(questions.get(1)).insert(0, questions.get(0)).append(questions.get(2));
        assertEquals(q0.getQuestions(), builder.build().getQuestions());
    }

    @Test
    public void builderSwap() {
        List<Question> questions = initQuestionList();
        Quiz q0 = new Quiz("title", questions);
        Quiz.Builder builder = new Quiz.Builder();
        builder.append(questions.get(2))
                .append(questions.get(1))
                .append(questions.get(0))
                .swap(0, 2);
        assertEquals(q0.getQuestions(), builder.build().getQuestions());
    }

    @Test
    public void builderRemove() {
        List<Question> questions = initQuestionList();
        Quiz q0 = new Quiz("title", questions);
        Quiz.Builder builder = new Quiz.Builder();
        builder.append(questions.get(0))
                .append(questions.get(0))
                .append(questions.get(1))
                .append(questions.get(2))
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
        builder.append(null);
    }

    @Test(expected = IllegalStateException.class)
    public void builderAddFail2Test() {
        Quiz.Builder builder = new Quiz.Builder();
        builder.build();
        builder.insert(0, null);
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

    @Test(expected = IllegalArgumentException.class)
    public void builderTreasureHuntFail1() {
        Quiz.Builder builder = new Quiz.Builder(true);
        builder.append(new Question("", "", answerFormat));
    }

    @Test(expected = IllegalArgumentException.class)
    public void builderTreasureHuntFail2() {
        Quiz.Builder builder = new Quiz.Builder();
        builder.append(new Question("", "", answerFormat, new Location(""), 1));
    }
}
