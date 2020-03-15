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

import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import ch.epfl.qedit.backend.auth.AuthenticationFactory;
import ch.epfl.qedit.backend.auth.MockAuthService;
import ch.epfl.qedit.model.User;
import ch.epfl.qedit.view.LoginActivity;
import ch.epfl.qedit.view.ViewRoleActivity;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4ClassRunner.class)
public class LoginActivityTest {

    private IdlingResource idlingResource;

    @Rule
    public final IntentsTestRule<LoginActivity> testRule =
            new IntentsTestRule<>(LoginActivity.class, false, false);

    @Before
    public void init() {
        MockAuthService authService = new MockAuthService();
        idlingResource = authService.getIdlingResource();
        IdlingRegistry.getInstance().register(idlingResource);
        AuthenticationFactory.setInstance(authService);
        testRule.launchActivity(null);
    }

    @After
    public void cleanup() {
        IdlingRegistry.getInstance().unregister(idlingResource);
        testRule.finishActivity();
    }

    private void performLogin(String token) {
        onView(withId(R.id.login_token)).perform((typeText(token))).perform(closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());
    }

    private void testLoginSuccessful(String token, User user) {
        performLogin(token);
        intended(
                allOf(
                        hasComponent(ViewRoleActivity.class.getName()),
                        hasExtra(LoginActivity.USER, user)));
    }

    private void testLoginFailed(String token, int toastStringId) {
        performLogin(token);
        LoginActivity activity = testRule.getActivity();
        onView(withText(toastStringId))
                .inRoot(withDecorView(not(is(activity.getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
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
