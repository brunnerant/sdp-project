package ch.epfl.qedit.home;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.espresso.matcher.ViewMatchers;
import ch.epfl.qedit.R;
import ch.epfl.qedit.view.home.HomeInfoFragment;
import com.android21buttons.fragmenttestrule.FragmentTestRule;
import com.google.firebase.firestore.auth.User;

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

    public void testUserIsDisplayedCorrectly(
            String firstName, String lastName, User.Role role, int roleString) {
        User user = new User(firstName, lastName, role);
        String greetingString =
                testRule.getActivity().getString(R.string.welcome)
                        + " "
                        + firstName
                        + " "
                        + lastName
                        + testRule.getActivity().getString(R.string.exclamation_point);
        onView(withId(R.id.greeting)).check(matches(withText(greetingString)));
        onView(withId(R.id.role))
                .check(matches(withText(testRule.getActivity().getString(roleString))));
    }

    @Test
    public void testParticipantIsDisplayedCorrectly() {
        testUserIsDisplayedCorrectly(
                "Bill", "Gates", User.Role.Participant, R.string.role_participant);
    }

    @Test
    public void testEditorIsDisplayedCorrectly() {
        testUserIsDisplayedCorrectly("John", "Cena", User.Role.Editor, R.string.role_editor);
    }

    @Test
    public void testAdministratorIsDisplayedCorrectly() {
        testUserIsDisplayedCorrectly(
                "The", "Rock", User.Role.Administrator, R.string.role_administrator);
    }
}
