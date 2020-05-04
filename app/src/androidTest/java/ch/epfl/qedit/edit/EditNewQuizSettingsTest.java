package ch.epfl.qedit.edit;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;

import androidx.test.espresso.intent.rule.IntentsTestRule;
import ch.epfl.qedit.R;
import ch.epfl.qedit.view.edit.EditNewQuizSettingsActivity;
import org.junit.Rule;
import org.junit.Test;

public class EditNewQuizSettingsTest {
    @Rule
    public final IntentsTestRule<EditNewQuizSettingsActivity> testRule =
            new IntentsTestRule<>(EditNewQuizSettingsActivity.class);

    @Test
    public void testEnterTitle() {
        onView(withId(R.id.edit_quiz_title)).check(matches(withText("")));

        onView(withId(R.id.edit_quiz_title))
                .perform((typeText("Title")))
                .perform(closeSoftKeyboard());
        onView(withId(R.id.edit_quiz_title)).check(matches(withText("Title")));
    }

    @Test
    public void testLanguageSpinner() {
        onView(withId(R.id.edit_language_selection)).perform(click());
        onData(anything()).atPosition(0).perform(click());
        onView(withId(R.id.edit_language_selection)).check(matches(withSpinnerText("English")));
    }

    @Test
    public void testButtonsIntent() {
        onView(withId(R.id.edit_quiz_title))
                .perform((typeText("Title")))
                .perform(closeSoftKeyboard());
        onView(withId(R.id.button_start_editing)).perform(click());
        // intended(allOf(hasComponent(EditQuizActivity.class.getName()))); TODO
    }
}
