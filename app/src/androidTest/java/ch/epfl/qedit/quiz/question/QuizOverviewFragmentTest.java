package ch.epfl.qedit.quiz.question;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;
import static org.junit.Assert.assertEquals;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import ch.epfl.qedit.R;
import ch.epfl.qedit.quiz.QuizFragmentsTestUsingDB;
import ch.epfl.qedit.view.quiz.QuizOverviewFragment;
import ch.epfl.qedit.viewmodel.QuizViewModel;
import com.android21buttons.fragmenttestrule.FragmentTestRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4ClassRunner.class)
public class QuizOverviewFragmentTest extends QuizFragmentsTestUsingDB {
    private QuizViewModel model;

    @Rule
    public final FragmentTestRule<?, QuizOverviewFragment> testRule =
            FragmentTestRule.create(QuizOverviewFragment.class, false);

    @Before
    public void setup() {
        model = super.setup(testRule, new QuizOverviewFragment());
    }

    @After
    public void cleanup() {
        super.cleanup();
    }

    @Test
    public void testOverviewIsDisplayed() {
        onView(withId(R.id.question_list)).check(matches(isDisplayed()));
    }

    @Test
    public void testQuizOverviewIsProperlyShown() {
        onData(anything())
                .inAdapterView(withId(R.id.question_list))
                .atPosition(0)
                .check(matches(withText("1) The matches problem")));
    }

    @Test
    public void testFocusedQuestionChangesOnClick() {
        onData(anything()).inAdapterView(withId(R.id.question_list)).atPosition(0).perform(click());
        assertEquals(0, (int) model.getFocusedQuestion().getValue());

        onData(anything()).inAdapterView(withId(R.id.question_list)).atPosition(1).perform(click());
        assertEquals(1, (int) model.getFocusedQuestion().getValue());
    }
}
