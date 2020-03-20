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
import org.junit.Rule;
import org.junit.Test;

public class HomeInfoFragmentTest {
    @Rule
    public final FragmentTestRule<?, HomeInfoFragment> testRule =
            FragmentTestRule.create(HomeInfoFragment.class);

    public void init(User user) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("user", user);
        HomeInfoFragment homeInfoFragment = new HomeInfoFragment();
        homeInfoFragment.setArguments(bundle);
    }

    @After
    public void cleanup() {
        testRule.finishActivity();
    }

    public void testUserIsDisplayedCorrectly(User user, String greeting, String role) {
        init(user);
        onView(ViewMatchers.withId(R.id.greeting)).check(matches(withText(greeting)));
        onView(withId(R.id.role)).check(matches(withText(role)));
    }

    @Test
    public void testParticipantIsDisplayedCorrectly() {
        testUserIsDisplayedCorrectly(
                new User("Bill", "Gates", User.Role.Participant),
                "Bienvenue Bill Gates !",
                "Vous êtes un participant.");
    }

    @Test
    public void testEditorIsDisplayedCorrectly() {
        testUserIsDisplayedCorrectly(
                new User("John", "Cena", User.Role.Editor),
                "Bienvenue John Cena !",
                "Vous êtes un éditeur.");
    }

    @Test
    public void testAdministratorIsDisplayedCorrectly() {
        testUserIsDisplayedCorrectly(
                new User("The", "Rock", User.Role.Administrator),
                "Bienvenue The Rock !",
                "Vous êtes un administrateur.");
    }
}
