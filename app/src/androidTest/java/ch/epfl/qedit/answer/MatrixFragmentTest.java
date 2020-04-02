package ch.epfl.qedit.answer;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.qedit.view.quiz.QuestionFragment.ANSWER_FORMAT;
import static ch.epfl.qedit.view.quiz.QuestionFragment.ANSWER_MODEL;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;

import android.os.Bundle;
import androidx.lifecycle.ViewModelProvider;
import androidx.test.espresso.IdlingResource;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import ch.epfl.qedit.R;
import ch.epfl.qedit.model.answer.MatrixFormat;
import ch.epfl.qedit.model.answer.MatrixModel;
import ch.epfl.qedit.util.Util;
import ch.epfl.qedit.view.answer.MatrixFragment;
import ch.epfl.qedit.viewmodel.QuizViewModel;
import com.android21buttons.fragmenttestrule.FragmentTestRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4ClassRunner.class)
public class MatrixFragmentTest {
    private IdlingResource idlingResource;
    private final int MATRIX_DIM = 3;
    private QuizViewModel quizViewModel;
    private String answer = "1234";

    @Rule
    public final FragmentTestRule<?, MatrixFragment> testRule =
            FragmentTestRule.create(MatrixFragment.class, false, false);

    @Before
    public void init() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ANSWER_FORMAT, new MatrixFormat(MATRIX_DIM, MATRIX_DIM));
        final MatrixModel matrixModel = new MatrixModel(MATRIX_DIM, MATRIX_DIM);
        matrixModel.updateAnswer(MATRIX_DIM - 1, MATRIX_DIM - 1, answer);
        bundle.putSerializable(ANSWER_MODEL, matrixModel);

        MatrixFragment matrixFragment = new MatrixFragment();
        matrixFragment.setArguments(bundle);

        quizViewModel = new ViewModelProvider(testRule.getActivity()).get(QuizViewModel.class);
        quizViewModel.setQuiz(Util.createMockQuiz("Title"));
        quizViewModel.getFocusedQuestion().postValue(0);

        testRule.launchFragment(matrixFragment);
    }

    @After
    public void cleanup() {
        testRule.finishActivity();
    }

    @Test
    public void testTableIsDisplayed() {
        onView(withId(R.id.answersTable)).check(matches(isDisplayed()));
    }

    @Test
    public void testFieldsAreDisplayed() {
        for (int i = 0; i < MATRIX_DIM; ++i) {
            for (int j = 0; j < MATRIX_DIM; ++j) {
                onView(withId(testRule.getFragment().getId(i, j))).check(matches(isDisplayed()));
            }
        }
    }

    @Test
    public void testFieldsAreEmptyAtFirst() {
        for (int i = 0; i < MATRIX_DIM; ++i) {
            for (int j = 0; j < MATRIX_DIM; ++j) {
                if (i != MATRIX_DIM - 1 || j != MATRIX_DIM - 1) {
                    onView(withId(testRule.getFragment().getId(i, j))).check(matches(withText("")));
                }
            }
        }
    }

    public void type(String input, String expected) {
        int id = testRule.getFragment().getId(0, 0);

        onView(withId(id)).perform(click());
        onView(withId(id)).perform((typeText(input))).perform(closeSoftKeyboard());
        onView(withId(id)).check(matches(withText(expected)));
    }

    @Test
    public void testCanEnterNumbersInFields() {
        type(answer, answer);
    }

    @Test
    public void testCanOnlyHaveOneMinusSign() {
        type("--12", "-12");
    }

    @Test
    public void testCanOnlyHaveOneDecimalPoint() {
        type("23..2", "23.2");
    }

    @Test
    public void testCantEnterMoreDigitsThanMaxCharacters() {
        // MaxCharacters = 5 by default
        type("123456", "12345");
    }

    // @Test TODO
    public void testAnswerIsSavedInQuizViewModel() {
        assertNull(quizViewModel.getAnswers().getValue().get(0));

        int id = testRule.getFragment().getId(0, 0);
        onView(withId(id)).perform(click());

        onView(withId(id)).perform((typeText(answer))).perform(closeSoftKeyboard());

        assertNotNull(quizViewModel.getAnswers().getValue().get(0));
        assertEquals(
                answer,
                ((MatrixModel) quizViewModel.getAnswers().getValue().get(0)).getAnswer(0, 0));
    }

    // @Test TODO
    public void testAnswerIsLoadedFromModel() {
        int id = testRule.getFragment().getId(MATRIX_DIM - 1, MATRIX_DIM - 1);
        onView(withId(id)).check(matches(withText(answer)));
    }
}
