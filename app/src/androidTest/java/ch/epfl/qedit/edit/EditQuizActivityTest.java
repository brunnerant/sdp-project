package ch.epfl.qedit.edit;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.rule.ActivityTestRule;
import ch.epfl.qedit.R;
import ch.epfl.qedit.view.edit.EditQuizActivity;
import org.junit.Rule;
import org.junit.Test;

public class EditQuizActivityTest {
    @Rule
    public final ActivityTestRule<EditQuizActivity> testRule =
            new ActivityTestRule<>(EditQuizActivity.class);

    @Test
    public void testThatFragmentIsDisplayed() {
        onView(withId(R.id.quiz_overview_container)).check(matches(isDisplayed()));
    }
}
