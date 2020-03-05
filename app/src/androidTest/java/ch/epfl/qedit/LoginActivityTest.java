package ch.epfl.qedit;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.qedit.ui.login.LoginActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {
    @Rule
    public final ActivityTestRule<LoginActivity> loginActivityRule =
            new ActivityTestRule<>(LoginActivity.class);

    private void testLoginGeneral(String username, String password, String role) {
        onView(withId(R.id.username)).perform(typeText(username));
        onView(withId(R.id.password)).perform(typeText(password))
                .perform(closeSoftKeyboard());
        onView(withId(R.id.login)).perform(click());
        onView(withId(R.id.greeting)).check(matches(withText("Bienvenue " + username + "!")));
        onView(withId(R.id.role)).check(matches(withText("Vous êtes un " + role + ".")));
    }

    @Test
    public void testCanLoginAsAdmin() {
        testLoginGeneral("admin", "abcdef", "administrateur");
    }

    @Test
    public void testCanLoginAsParticipant() {
        testLoginGeneral("Bernard", "abcdefg", "participant");
    }
}
