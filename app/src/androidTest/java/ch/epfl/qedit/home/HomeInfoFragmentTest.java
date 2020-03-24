package ch.epfl.qedit.home;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.os.Bundle;
import androidx.test.espresso.matcher.ViewMatchers;
import ch.epfl.qedit.R;
import ch.epfl.qedit.model.User;
import ch.epfl.qedit.view.home.HomeInfoFragment;
import com.android21buttons.fragmenttestrule.FragmentTestRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class HomeInfoFragmentTest {
    private final User user = new User("Bill", "Gates", User.Role.Participant);

    @Rule
    public final FragmentTestRule<?, HomeInfoFragment> testRule =
            FragmentTestRule.create(HomeInfoFragment.class, false, false);

    @Before
    public void init() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("user", user);
        HomeInfoFragment homeInfoFragment = new HomeInfoFragment();
        homeInfoFragment.setArguments(bundle);

        testRule.launchFragment(homeInfoFragment);
    }

    @After
    public void cleanup() {
        testRule.finishActivity();
    }

    @Test
    public void testParticipantIsDisplayedCorrectly() {
        onView(ViewMatchers.withId(R.id.greeting))
                .check(matches(withText("Bienvenue Bill Gates !")));
        onView(withId(R.id.role)).check(matches(withText("Vous êtes un participant.")));
    }
}
