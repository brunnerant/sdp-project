package ch.epfl.qedit.login;

import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static ch.epfl.qedit.util.Util.clickOn;
import static ch.epfl.qedit.util.Util.onScrollView;
import static ch.epfl.qedit.view.home.HomeActivity.USER;
import static org.hamcrest.Matchers.allOf;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.intent.Intents;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.ActivityTestRule;
import ch.epfl.qedit.R;
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
public class LogInActivityTest extends LoginHelper {

    @Rule
    public final ActivityTestRule<LogInActivity> testRule =
            new ActivityTestRule<>(LogInActivity.class, false, false);

    @Before
    public void init() {
        super.init();
        testRule.launchActivity(null);
        Intents.init();
        Espresso.closeSoftKeyboard();
    }

    @After
    public void cleanup() {
        super.cleanup();
        Intents.release();
        testRule.finishActivity();
    }

    private void performLogIn(String email, String password) {
        onScrollView(R.id.field_email).perform((typeText(email))).perform(pressImeActionButton());
        onScrollView(R.id.field_password)
                .perform((typeText(password)))
                .perform(pressImeActionButton());
        clickOn(R.id.button_log_in, true);
    }

    private void fillField(int field, String stringToBeTyped) {
        onScrollView(field).perform((typeText(stringToBeTyped))).perform(closeSoftKeyboard());
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
        intended(hasComponent(SignUpActivity.class.getName()));
    }

    @Test
    public void testEmptyEmailCannotLogIn() {
        performLogIn("", "123456");
    }

    @Test
    public void testWrongEmailCannotLogIn() {
        fillField(R.id.field_email, "a");
    }

    @Test
    public void testEmptyPasswordCannotLogIn() {
        performLogIn("anthony@mock.test", "");
    }

    @Test
    public void testShortPasswordCannotLogIn() {
        fillField(R.id.field_password, "a");
    }

    @Test
    public void testNoUserExistingCannotLogIn() {
        testLogInFailed("test@test.com", "password");
    }
}
