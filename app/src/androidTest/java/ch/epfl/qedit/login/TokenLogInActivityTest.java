package ch.epfl.qedit.login;

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
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import ch.epfl.qedit.R;
import ch.epfl.qedit.backend.auth.AuthenticationFactory;
import ch.epfl.qedit.backend.auth.MockAuthService;
import ch.epfl.qedit.model.User;
import ch.epfl.qedit.view.home.HomeActivity;
import ch.epfl.qedit.view.login.TokenLogInActivity;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4ClassRunner.class)
public class TokenLogInActivityTest {

    private IdlingResource idlingResource;

    @Rule
    public final IntentsTestRule<TokenLogInActivity> testRule =
            new IntentsTestRule<>(TokenLogInActivity.class, false, false);

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
        onView(ViewMatchers.withId(R.id.field_token))
                .perform((typeText(token)))
                .perform(closeSoftKeyboard());
        onView(withId(R.id.button_log_in)).perform(click());
    }

    private void testLoginSuccessful(String token, User user) {
        performLogin(token);
        intended(
                allOf(
                        hasComponent(HomeActivity.class.getName()),
                        hasExtra(TokenLogInActivity.USER, user)));
    }

    private void testLoginFailed(String token, int toastStringId) {
        performLogin(token);
        TokenLogInActivity activity = testRule.getActivity();
        onView(withText(toastStringId))
                .inRoot(withDecorView(not(is(activity.getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testConnectionErrorCannotLogIn() {
        //noinspection SpellCheckingInspection
        testLoginFailed("fjd4ywnzcCcLHaVb7oKg", R.string.connection_error_message);
    }

    @Test
    public void testUnknownTokenCannotLogIn() {
        testLoginFailed("unknownToken", R.string.wrong_token_message);
    }

    @Test
    public void testEmptyTokenCannotLogIn() {
        testLoginFailed("", R.string.empty_token_message);
    }

    @Test
    public void testParticipantCanLogIn() {
        //noinspection SpellCheckingInspection
        testLoginSuccessful("fjd4ywnzXCXLHaVb7oKg", new User("Marcel", "Doe"));
    }

    @Test
    public void testEditorCanLogIn() {
        //noinspection SpellCheckingInspection
        testLoginSuccessful("R4rXRVU3EMkgm5YEW52Q", new User("Cosme", "Jordan"));
    }

    @Test
    public void testAdministratorCanLogIn() {
        testLoginSuccessful("v5ns9OMqV4hH7jwD8S5w", new User("Anthony", "Iozzia"));
    }
}
