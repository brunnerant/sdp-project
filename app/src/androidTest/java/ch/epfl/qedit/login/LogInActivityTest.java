package ch.epfl.qedit.login;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static ch.epfl.qedit.util.Util.clickOn;
import static ch.epfl.qedit.util.Util.onScrollView;
import static ch.epfl.qedit.view.login.Util.USER;
import static org.hamcrest.Matchers.allOf;

import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import ch.epfl.qedit.R;
import ch.epfl.qedit.backend.auth.AuthenticationFactory;
import ch.epfl.qedit.backend.auth.MockAuthService;
import ch.epfl.qedit.backend.database.DatabaseFactory;
import ch.epfl.qedit.backend.database.MockDBService;
import ch.epfl.qedit.model.User;
import ch.epfl.qedit.view.home.HomeActivity;
import ch.epfl.qedit.view.login.LogInActivity;
import ch.epfl.qedit.view.login.SignUpActivity;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4ClassRunner.class)
public class LogInActivityTest {

    private IdlingResource idlingResource;

    @Rule
    public final IntentsTestRule<LogInActivity> testRule =
            new IntentsTestRule<>(LogInActivity.class, false, false);

    @Before
    public void init() {
        MockAuthService authService = new MockAuthService();
        MockDBService dbService = new MockDBService();
        idlingResource = authService.getIdlingResource();
        IdlingRegistry.getInstance().register(idlingResource);
        AuthenticationFactory.setInstance(authService);
        DatabaseFactory.setInstance(dbService);
        testRule.launchActivity(null);
    }

    @After
    public void cleanup() {
        IdlingRegistry.getInstance().unregister(idlingResource);
        testRule.finishActivity();
    }

    private void performLogIn(String email, String password) {
        onScrollView(R.id.field_email).perform((typeText(email))).perform(pressImeActionButton());
        onScrollView(R.id.field_password)
                .perform((typeText(password)))
                .perform(pressImeActionButton());
        clickOn(R.id.button_log_in, true);
    }

    @SuppressWarnings("SameParameterValue")
    private void testLogInSuccessful(String email, String password, User user) {
        performLogIn(email, password);
        intended(allOf(hasComponent(HomeActivity.class.getName()), hasExtra(USER, user)));
    }

    @SuppressWarnings("SameParameterValue")
    private void testLogInFailed(String email, String password) {
        performLogIn(email, password);
    }

    @Test
    public void testCanLogIn() {
        testLogInSuccessful("anthony@mock.test", "123456", MockDBService.createAnthony());
    }

    @Test
    public void testCanLogInAndLogOut() {
        testLogInSuccessful("anthony@mock.test", "123456", MockDBService.createAnthony());

        clickOn(R.id.log_out, false);
        //noinspection unchecked
        intended(allOf(hasComponent(LogInActivity.class.getName())));
    }

    @Test
    public void testSignUpInstead() {
        clickOn(R.id.sign_up_instead, true);
        //noinspection unchecked
        intended(allOf(hasComponent(SignUpActivity.class.getName())));
    }

    @Test
    public void testEmptyEmailCannotLogIn() {
        onView(withId(R.id.field_email)).perform((typeText(""))).perform(closeSoftKeyboard());
        onView(withId(R.id.field_password))
                .perform((typeText("123456")))
                .perform(closeSoftKeyboard());

        clickOn(R.id.button_log_in, true);
    }

    @Test
    public void testWrongEmailCannotLogIn() {
        onView(withId(R.id.field_email)).perform((typeText("a"))).perform(closeSoftKeyboard());

        clickOn(R.id.button_log_in, true);
    }

    @Test
    public void testEmptyPasswordCannotLogIn() {
        onView(withId(R.id.field_email))
                .perform((typeText("anthony@mock.test")))
                .perform(closeSoftKeyboard());
        onView(withId(R.id.field_password)).perform((typeText(""))).perform(closeSoftKeyboard());

        clickOn(R.id.button_log_in, true);
    }

    @Test
    public void testShortPasswordCannotLogIn() {
        onView(withId(R.id.field_password)).perform((typeText("a"))).perform(closeSoftKeyboard());

        clickOn(R.id.button_log_in, true);
    }

    @Test
    public void testNoUserExistingCannotLogIn() {
        testLogInFailed("test@test.com", "password");
    }
}
