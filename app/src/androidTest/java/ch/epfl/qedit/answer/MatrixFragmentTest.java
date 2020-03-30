package ch.epfl.qedit.answer;

import android.os.Bundle;
import androidx.lifecycle.ViewModelProvider;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import ch.epfl.qedit.R;
import ch.epfl.qedit.backend.database.DatabaseFactory;
import ch.epfl.qedit.backend.database.MockDBService;
import ch.epfl.qedit.model.MatrixFormat;
import ch.epfl.qedit.view.answer.MatrixFragment;
import ch.epfl.qedit.viewmodel.QuizViewModel;
import com.android21buttons.fragmenttestrule.FragmentTestRule;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.android21buttons.fragmenttestrule.FragmentTestRule;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

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
        bundle.putSerializable("m0", MatrixFormat.createMatrix3x3());
        MatrixFragment matrixFragment = new MatrixFragment();
        matrixFragment.setArguments(bundle);
      
        model = new ViewModelProvider(testRule.getActivity()).get(QuizViewModel.class);
        // model.getAnswers().setValue(new HashMap<Integer, HashMap<Integer, Float>>());

        testRule.launchFragment(matrixFragment);
    }

    @After
    public void cleanup() {
        testRule.finishActivity();
    }


    private void lockWait(long time) {
        try {
            lock.await(time, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void dummyTest() {
        Assert.assertEquals(true, true);

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
      
    /*@Test
    public void testAnswersStoredCorrectly() {

       for (int i = 0; i < 20; i++) {
            model.getAnswers().getValue().put(i, new HashMap<Integer, Float>());
        }

        int id = testRule.getFragment().getId(0, 0);

        onView(withId(id)).perform(typeText("47.3"));
        Assert.assertEquals(model.getAnswers().getValue().get(0).get(id), (Float) 47.3f);
    }*/
      
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
