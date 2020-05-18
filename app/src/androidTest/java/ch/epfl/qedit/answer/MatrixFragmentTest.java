package ch.epfl.qedit.answer;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.qedit.view.quiz.QuestionFragment.ANSWER_FORMAT;
import static ch.epfl.qedit.view.quiz.QuestionFragment.ANSWER_MODEL;
import static org.junit.Assert.assertEquals;

import android.os.Bundle;
import androidx.lifecycle.ViewModelProvider;
import androidx.test.espresso.ViewInteraction;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import ch.epfl.qedit.model.answer.AnswerModel;
import ch.epfl.qedit.model.answer.MatrixFormat;
import ch.epfl.qedit.model.answer.MatrixModel;
import ch.epfl.qedit.view.answer.MatrixFragment;
import ch.epfl.qedit.viewmodel.QuizViewModel;
import com.android21buttons.fragmenttestrule.FragmentTestRule;
import java.util.HashMap;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4ClassRunner.class)
public class MatrixFragmentTest {
    private QuizViewModel quizViewModel;

    @Rule
    public final FragmentTestRule<?, MatrixFragment> testRule =
            FragmentTestRule.create(MatrixFragment.class, false, false);

    @Before
    public void init() {
        MatrixFormat format =
                new MatrixFormat.Builder(2, 3)
                        .withField(0, 0, MatrixFormat.Field.preFilledField("pre-filled"))
                        .withField(0, 1, MatrixFormat.Field.textField("h1", 3))
                        .withField(0, 2, MatrixFormat.Field.numericField(false, false, "h2"))
                        .withField(1, 0, MatrixFormat.Field.numericField(false, true, "h3"))
                        .withField(1, 1, MatrixFormat.Field.numericField(true, false, "h4"))
                        .withField(1, 2, MatrixFormat.Field.numericField(true, true, "h5"))
                        .build();
        MatrixModel model = new MatrixModel(2, 3);
        model.updateAnswer(0, 1, "abc");
        model.updateAnswer(1, 1, "3.14");

        Bundle bundle = new Bundle();
        bundle.putSerializable(ANSWER_FORMAT, format);
        bundle.putSerializable(ANSWER_MODEL, model);

        MatrixFragment matrixFragment = new MatrixFragment();
        matrixFragment.setArguments(bundle);

        HashMap<Integer, AnswerModel> answers = new HashMap<>();
        answers.put(0, model);

        quizViewModel = new ViewModelProvider(testRule.getActivity()).get(QuizViewModel.class);
        quizViewModel.getAnswers().postValue(answers);
        quizViewModel.getFocusedQuestion().postValue(0);

        testRule.launchFragment(matrixFragment);
    }

    @After
    public void cleanup() {
        testRule.finishActivity();
    }

    private ViewInteraction onField(int i, int j) {
        return onView(withId(testRule.getFragment().getId(i, j)));
    }

    private void assertHint(int i, int j, String hint) {
        onField(i, j).perform(clearText());
        onField(i, j).check(matches(withHint(hint)));
    }

    private void assertTyping(int i, int j, String typed, String result) {
        onField(i, j).perform(clearText());
        onField(i, j).perform(typeText(typed));
        onField(i, j).check(matches(withText(result)));
    }

    private void assertViewModel(int i, int j, String typed) {
        onField(i, j).perform(clearText());
        onField(i, j).perform(typeText(typed));
        MatrixModel answer = (MatrixModel) quizViewModel.getAnswers().getValue().get(0);
        assertEquals(typed, answer.getAnswer(i, j));
    }

    @Test
    public void testFieldsAreCorrectlyInitialized() {
        onField(0, 0).check(matches(withText("pre-filled")));
        onField(0, 1).check(matches(withText("abc")));
        onField(0, 2).check(matches(withText("")));
        onField(1, 0).check(matches(withText("")));
        onField(1, 1).check(matches(withText("3.14")));
        onField(1, 2).check(matches(withText("")));
    }

    @Test
    public void testHintsAreCorrect() {
        assertHint(0, 1, "h1");
        assertHint(0, 2, "h2");
        assertHint(1, 0, "h3");
        assertHint(1, 1, "h4");
        assertHint(1, 2, "h5");
    }

    @Test
    public void testTextField() {
        assertTyping(0, 1, "abc", "abc");
        //noinspection SpellCheckingInspection
        assertTyping(0, 1, "abcd", "abc");
        assertTyping(0, 1, "=-)", "=-)");
    }

    @Test
    public void testUnsignedIntField() {
        assertTyping(0, 2, "324", "324");
        assertTyping(0, 2, "-23", "23");
        assertTyping(0, 2, "23.2", "232");
        assertTyping(0, 2, "x1y2", "12");
        assertTyping(0, 2, "10901", "1090");
    }

    @Test
    public void testSignedIntField() {
        assertTyping(1, 0, "-12", "-12");
        assertTyping(1, 0, "--12", "-12");
        assertTyping(1, 0, "-12.2", "-122");
        assertTyping(1, 0, "-12222", "-1222");
        assertTyping(1, 0, "-12a2", "-122");
    }

    @Test
    public void testUnsignedFloatField() {
        assertTyping(1, 1, "3.14", "3.14");
        assertTyping(1, 1, "-3.14", "3.14");
        assertTyping(1, 1, "3.a14", "3.14");
        assertTyping(1, 1, "3.14444", "3.1444");
    }

    @Test
    public void testSignedFloatField() {
        assertTyping(1, 2, "-3.14", "-3.14");
        assertTyping(1, 2, "-3a.1x4", "-3.14");
        assertTyping(1, 2, "--3.14", "-3.14");
        assertTyping(1, 2, "-3.14444", "-3.1444");
    }

    @Test
    public void testAnswerIsSavedInQuizViewModel() {
        assertViewModel(0, 1, "abc");
        assertViewModel(0, 2, "12");
        assertViewModel(1, 0, "-12");
        assertViewModel(1, 1, "12.1");
        assertViewModel(1, 2, "-12.1");
    }
}
