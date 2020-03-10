package ch.epfl.qedit;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.qedit.view.MainActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4ClassRunner.class)
public class MainActivityTest {
    @Rule
    public final ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void testPressButtonQuizTest() {
        onView(withId(R.id.buttonOpenQuiz)).perform(click());
        onView(withId(R.id.question_title)).check(matches(withText("1) Question test")));
    }
}
