package ch.epfl.qedit.home;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;

import androidx.test.espresso.intent.Intents;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import ch.epfl.qedit.R;
import ch.epfl.qedit.view.home.HomeQuizListFragment;
import com.android21buttons.fragmenttestrule.FragmentTestRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4ClassRunner.class)
public class HomeQuizListFragmentTest extends HomeFragmentsTestUsingDB {
    @Rule
    public final FragmentTestRule<?, HomeQuizListFragment> testRule =
            FragmentTestRule.create(HomeQuizListFragment.class, false, false);

    @Before
    public void setup() {
        Intents.init();
        super.setup(testRule, new HomeQuizListFragment());
    }

    @After
    public void cleanup() {
        Intents.release();
        super.cleanup();
    }

    @Test
    public void testQuizListIsProperlyLoaded() {
        onData(anything())
                .inAdapterView(withId(R.id.home_quiz_list))
                .atPosition(0)
                .check(matches(withText("Qualification EPFL")));
    }

    @Test
    public void testClickOnQuizLaunchesQuizActivity() {
        onData(anything())
                .inAdapterView(withId(R.id.home_quiz_list))
                .atPosition(0)
                .perform(click());
    }
}
