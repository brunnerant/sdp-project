package ch.epfl.qedit.home;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.qedit.util.Util.clickOn;
import static org.hamcrest.Matchers.not;

import androidx.test.espresso.action.ViewActions;
import ch.epfl.qedit.R;
import ch.epfl.qedit.util.Util;
import ch.epfl.qedit.view.home.HomeInfoFragment;
import com.android21buttons.fragmenttestrule.FragmentTestRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class HomeInfoFragmentTest extends HomeFragmentsTestUsingDB {
    @Rule
    public final FragmentTestRule<?, HomeInfoFragment> testRule =
            FragmentTestRule.create(HomeInfoFragment.class, false, false);

    public HomeInfoFragmentTest() {
        super(0);
    }

    @Before
    public void setup() {
        setup(testRule, new HomeInfoFragment());
    }

    @After
    public void cleanup() {
        cleanup(testRule);
    }

    private void testUserIsDisplayedCorrectly(String firstName, String lastName) {
        String greetingString =
                testRule.getActivity().getString(R.string.welcome)
                        + " "
                        + firstName
                        + " "
                        + lastName
                        + testRule.getActivity().getString(R.string.exclamation_point);
        onView(withId(R.id.greeting)).check(matches(withText(greetingString)));
    }

    @Test
    public void testParticipantIsDisplayedCorrectly() {
        testUserIsDisplayedCorrectly("Jon", "Snow");
    }

    @Test
    public void testStatsIsNotDisplayed() {
        clickOn(R.id.display_stats, false);
        onView(withId(R.id.stats)).check(matches(not(isDisplayed())));
        onView(withId(R.id.display_stats)).check(matches(withText(R.string.display_stats)));
    }

    @Test
    public void testStatsIsDisplayed() {
        Util.isDisplayed(R.id.stats, false);
        onView(withId(R.id.display_stats)).check(matches(withText(R.string.hide_stats)));
        textIsDisplay(Integer.toString(score));
        textIsDisplay(Integer.toString(successes));
        textIsDisplay(Integer.toString(attempts));
        textIsDisplay(testRule.getActivity().getString(R.string.score));
        textIsDisplay(testRule.getActivity().getString(R.string.successes));
        textIsDisplay(testRule.getActivity().getString(R.string.attempts));
    }

    private void textIsDisplay(String str) {
        onView(withText(str)).perform(ViewActions.scrollTo()).check(matches(isDisplayed()));
    }
}
