package ch.epfl.qedit;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.Is.is;

import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import ch.epfl.qedit.model.User;
import ch.epfl.qedit.view.LoginActivity;
import ch.epfl.qedit.view.ViewRoleActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {
    @Rule
    public final IntentsTestRule<LoginActivity> intentsTestRule =
            new IntentsTestRule<>(LoginActivity.class);

    private void performLogin(String token) {
        onView(withId(R.id.login_token)).perform((typeText(token))).perform(closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());
    }

    private void testLoginSuccessful(String token, User user) {
        performLogin(token);
        intended(allOf(
            hasComponent(ViewRoleActivity.class.getName()),
            hasExtra(LoginActivity.USER, user)
        ));
    }

    private void testLoginFailed(String token, int toastStringId) {
        performLogin(token);
        LoginActivity activity = intentsTestRule.getActivity();
        onView(withText(toastStringId)).
                inRoot(withDecorView(not(is(activity.getWindow().getDecorView())))).
                check(matches(isDisplayed()));
    }

    @Test
    public void testNicolasCannotLogIn() {
        testLoginFailed("nicolas", R.string.connection_error_message);
    }

    @Test
    public void testUnknownTokenCannotLogIn() {
        testLoginFailed("unknownToken", R.string.wrong_token_message);
    }

    @Test
    public void testParticipantCanLogIn() {
        testLoginSuccessful("nathan", new User("nathan", "greslin", User.Role.Participant));
    }

    @Test
    public void testEditorCanLogIn() {
        testLoginSuccessful("anthony", new User("anthony", "iozzia", User.Role.Editor));
    }

    @Test
    public void testAdministratorCanLogIn() {
        testLoginSuccessful("antoine", new User("antoine", "brunner", User.Role.Administrator));
    }
}
