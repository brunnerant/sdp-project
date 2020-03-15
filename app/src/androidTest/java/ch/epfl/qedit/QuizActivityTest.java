package ch.epfl.qedit;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.ActivityTestRule;
import ch.epfl.qedit.view.QuizActivity;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4ClassRunner.class)
public class QuizActivityTest {

    @Rule
    public final ActivityTestRule<QuizActivity> mActivityRule =
            new ActivityTestRule<>(QuizActivity.class);

    @Test
    public void testDisplayTheRightTitle() {
        onView(withId(R.id.question_title)).check(matches(withText("1) The matches problem")));
    }

    @Test
    public void testDisplayTheRightText() {
        onView(withId(R.id.question_display))
                .check(matches(withText("How many matches can fit in a shoe of size 43?")));
    }
}
