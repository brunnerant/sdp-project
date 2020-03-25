package ch.epfl.qedit.quiz.question;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.not;

import android.view.View;
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
            FragmentTestRule.create(QuizOverviewFragment.class, false, false);

    @Before
    public void setup() {
        model = super.setup(testRule, new QuizOverviewFragment());
    }

    @After
    public void cleanup() {
        super.cleanup();
    }

    @Test
    public void testOverviewIsLoading() {
        onView(withId(R.id.question_list))
                .check(matches(isDisplayed()))
                .check(matches(not(hasDescendant(any(View.class)))));
    }

    @Test
    public void testQuizIsProperlyLoaded() {
        model.loadQuiz("quiz0");
        onData(anything())
                .inAdapterView(withId(R.id.question_list))
                .atPosition(0)
                .check(matches(withText("1) Banane")));
    }
}
