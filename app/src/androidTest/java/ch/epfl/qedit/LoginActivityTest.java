package ch.epfl.qedit;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import ch.epfl.qedit.view.LoginActivity;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {
    @Rule
    public final ActivityTestRule<LoginActivity> loginActivityRule =
            new ActivityTestRule<>(LoginActivity.class);

    private void testLoginGeneral(String username, String password, String role) {
        onView(withId(R.id.login_username_text)).perform(typeText(username));
        onView(withId(R.id.login_password_text))
                .perform(typeText(password))
                .perform(closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());
        onView(withId(R.id.greeting)).check(matches(withText("Bienvenue " + username + " !")));
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
