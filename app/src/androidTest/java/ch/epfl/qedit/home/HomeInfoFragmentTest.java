package ch.epfl.qedit.home;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import ch.epfl.qedit.R;
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

    @Before
    public void setup() {
        super.setup(testRule, new HomeInfoFragment());
    }

    @After
    public void cleanup() {
        super.cleanup();
    }

    @Test
    public void testParticipantIsDisplayedCorrectly() {
        onView(withId(R.id.greeting)).check(matches(withText("Bienvenue Jon Snow !")));
        onView(withId(R.id.role)).check(matches(withText("Vous êtes un participant.")));
    }
}
