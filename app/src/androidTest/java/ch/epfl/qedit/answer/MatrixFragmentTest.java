package ch.epfl.qedit.answer;

import android.os.Bundle;

import androidx.lifecycle.ViewModelProvider;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.android21buttons.fragmenttestrule.FragmentTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import ch.epfl.qedit.R;
import ch.epfl.qedit.backend.database.DatabaseFactory;
import ch.epfl.qedit.backend.database.MockDBService;
import ch.epfl.qedit.model.MatrixFormat;
import ch.epfl.qedit.util.Util;
import ch.epfl.qedit.view.answer.MatrixFragment;
import ch.epfl.qedit.view.quiz.QuestionFragment;
import ch.epfl.qedit.viewmodel.QuizViewModel;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4ClassRunner.class)
public class MatrixFragmentTest {
    private QuizViewModel model;
    private CountDownLatch lock = new CountDownLatch(1);
    final int MATRIX_DIM = 3;

    @Rule
    public final FragmentTestRule<?, MatrixFragment> testRule =
            FragmentTestRule.create(MatrixFragment.class, false, false);

    @Before
    public void init() {
        MockDBService dbService = new MockDBService();
        DatabaseFactory.setInstance(dbService);

        Bundle bundle = new Bundle();
        bundle.putSerializable(QuestionFragment.ANSWER_FORMAT, MatrixFormat.createMatrix3x3());
        MatrixFragment matrixFragment = new MatrixFragment();
        matrixFragment.setArguments(bundle);

        model = new ViewModelProvider(testRule.getActivity()).get(QuizViewModel.class);
        model.setQuiz(Util.createMockQuiz("Title"));

        testRule.launchFragment(matrixFragment);
    }

    @After
    public void cleanup() {
        testRule.finishActivity();
    }

    private void lockWait(long time) { // TODO
        try {
            lock.await(time, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
                onView(withId(testRule.getFragment().getId(i, j))).check(matches(withText("")));
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
        type("1232", "1232");
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
        // MaxCharacters = 5 for MatrixFormat.createMatrix3x3()
        type("123456", "12345");
    }
}
