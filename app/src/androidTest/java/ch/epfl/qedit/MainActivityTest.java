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
import ch.epfl.qedit.view.MainActivity;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    @Rule
    public final ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void testPressButtonQuizTest() {
        onView(withId(R.id.buttonOpenQuiz)).perform(click());
        onView(withId(R.id.question_title)).check(matches(withText("1) Question test")));
    }

    @Test
    public void testPressButtonLogin() {
        onView(withId(R.id.buttonLogin)).perform(click());
        onView(withId(R.id.login_username_text)).perform(typeText("John"));
        onView(withId(R.id.login_password_text))
                .perform(typeText("1234"))
                .perform(closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());
        onView(withId(R.id.greeting)).check(matches(withText("Bienvenue John Doe !")));
        onView(withId(R.id.role)).check(matches(withText("Vous êtes un participant.")));
    }
}
