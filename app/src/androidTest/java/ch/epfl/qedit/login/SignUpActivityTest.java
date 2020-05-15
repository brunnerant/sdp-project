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
import static ch.epfl.qedit.view.login.Util.USER;
import static org.hamcrest.Matchers.allOf;

import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.espresso.matcher.ViewMatchers;
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
public class SignUpActivityTest {

    private IdlingResource idlingResource;

    @Rule
    public final IntentsTestRule<SignUpActivity> testRule =
            new IntentsTestRule<>(SignUpActivity.class, false, false);

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

    private void fillFields(
            String firstName,
            String lastName,
            String email,
            String password,
            String passwordConfirmation) {
        onView(ViewMatchers.withId(R.id.field_first_name))
                .perform((typeText(firstName)))
                .perform(pressImeActionButton());
        onView(ViewMatchers.withId(R.id.field_last_name))
                .perform((typeText(lastName)))
                .perform(pressImeActionButton());
        onView(ViewMatchers.withId(R.id.field_email))
                .perform((typeText(email)))
                .perform(pressImeActionButton());
        onView(ViewMatchers.withId(R.id.field_password))
                .perform((typeText(password)))
                .perform(pressImeActionButton());
        onView(ViewMatchers.withId(R.id.field_password_confirmation))
                .perform((typeText(passwordConfirmation)))
                .perform(pressImeActionButton());
    }

    private void performSignUp(
            String firstName,
            String lastName,
            String email,
            String password,
            String passwordConfirmation) {
        fillFields(firstName, lastName, email, password, passwordConfirmation);
        clickOn(R.id.button_sign_up, true);
    }

    private void testSignUpSuccessful(
            String firstName,
            String lastName,
            String email,
            String password,
            String passwordConfirmation,
            User user) {
        performSignUp(firstName, lastName, email, password, passwordConfirmation);
        intended(allOf(hasComponent(LogInActivity.class.getName())));
    }

    private void testSignUpFailed(
            String firstName,
            String lastName,
            String email,
            String password,
            String passwordConfirmation) {
        performSignUp(firstName, lastName, email, password, passwordConfirmation);
    }

    private void performLogIn(String email, String password) {
        onView(ViewMatchers.withId(R.id.field_email))
                .perform((typeText(email)))
                .perform(pressImeActionButton());
        onView(ViewMatchers.withId(R.id.field_password))
                .perform((typeText(password)))
                .perform(pressImeActionButton());
        clickOn(R.id.button_log_in, true);
    }

    private void testLogInSuccessful(String email, String password, User user) {
        performLogIn(email, password);
        intended(allOf(hasComponent(HomeActivity.class.getName()), hasExtra(USER, user)));
    }

    private void testEmptyEntryCannotSignUp(int id) {
        testWrongEntryCannotSignUp(id, "");
    }

    private void testWrongEntryCannotSignUp(int id, String stringToBeTyped) {
        fillFields("Balboa", "Bark", "balboa.bark@river.tree", "honeyToast", "honeyToast");
        onView(withId(id)).perform((typeText(stringToBeTyped))).perform(closeSoftKeyboard());

        clickOn(R.id.button_sign_up, true);
    }

    @Test
    public void testCanSignUp() {
        User user = new User("Balboa", "Bark");
        testSignUpSuccessful(
                "Balboa", "Bark", "balboa.bark@river.tree", "honeyToast", "honeyToast", user);
        testLogInSuccessful("balboa.bark@river.tree", "honeyToast", user);
    }

    @Test
    public void testLogInInstead() {
        clickOn(R.id.log_in_instead, true);
        intended(allOf(hasComponent(LogInActivity.class.getName())));
    }

    @Test
    public void testEmptyFirstNameCannotSignUp() {
        testEmptyEntryCannotSignUp(R.id.field_first_name);
    }

    @Test
    public void testEmptyLastNameCannotSignUp() {
        testEmptyEntryCannotSignUp(R.id.field_last_name);
    }

    @Test
    public void testEmptyEmailCannotSignUp() {
        testEmptyEntryCannotSignUp(R.id.field_email);
    }

    @Test
    public void testEmptyPasswordCannotSignUp() {
        testEmptyEntryCannotSignUp(R.id.field_password);
    }

    @Test
    public void testEmptyPasswordConfirmationCannotSignUp() {
        testEmptyEntryCannotSignUp(R.id.field_password_confirmation);
    }

    @Test
    public void testWrongFirstNameCannotSignUp() {
        testWrongEntryCannotSignUp(R.id.field_first_name, "$#");
    }

    @Test
    public void testWrongLastNameCannotSignUp() {
        testWrongEntryCannotSignUp(R.id.field_last_name, "$#");
    }

    @Test
    public void testWrongEmailCannotSignUp() {
        testWrongEntryCannotSignUp(R.id.field_email, "a");
    }

    @Test
    public void testWrongPasswordCannotSignUp() {
        testWrongEntryCannotSignUp(R.id.field_password, "1234");
    }

    @Test
    public void testWrongPasswordConfirmationCannotSignUp() {
        testWrongEntryCannotSignUp(R.id.field_password_confirmation, "1234");
    }

    @Test
    public void testWrongPasswordDifferentCannotSignUp() {
        fillFields("Balboa", "Bark", "balboa.bark@river.tree", "honeyToast", "honeyToast");
        onView(withId(R.id.field_password))
                .perform((typeText("1234567")))
                .perform(closeSoftKeyboard());
        onView(withId(R.id.field_password_confirmation))
                .perform((typeText("12345678")))
                .perform(closeSoftKeyboard());

        clickOn(R.id.button_sign_up, true);
    }

    @Test
    public void testUserAlreadyExistsCannotSignUp() {
        testSignUpFailed("Anthony", "Iozzia", "anthony@mock.test", "123456", "123456");
    }
}
