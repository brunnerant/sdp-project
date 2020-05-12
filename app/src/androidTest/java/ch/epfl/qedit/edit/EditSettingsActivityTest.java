package ch.epfl.qedit.edit;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.qedit.model.StringPool.TITLE_ID;
import static ch.epfl.qedit.util.Util.clickOn;
import static ch.epfl.qedit.util.Util.onDialog;
import static ch.epfl.qedit.view.edit.EditSettingsActivity.QUIZ_BUILDER;
import static ch.epfl.qedit.view.home.HomeQuizListFragment.STRING_POOL;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.AllOf.allOf;

import android.content.Intent;
import android.os.Bundle;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import ch.epfl.qedit.R;
import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.model.StringPool;
import ch.epfl.qedit.view.edit.EditQuizActivity;
import ch.epfl.qedit.view.edit.EditSettingsActivity;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class EditSettingsActivityTest {
    @Rule
    public final IntentsTestRule<EditSettingsActivity> testRule =
            new IntentsTestRule<>(EditSettingsActivity.class, false, false);

    @Before
    public void setUp() {
        StringPool stringPool = new StringPool();
        stringPool.update(TITLE_ID, "Ti");

        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable(STRING_POOL, stringPool);
        intent.putExtras(bundle);

        testRule.launchActivity(intent);
        Espresso.closeSoftKeyboard();
    }

    @After
    public void cleanUp() {
        testRule.finishActivity();
    }

    @Test
    public void testEnterTitle() {
        onView(withId(R.id.edit_quiz_title)).check(matches(withText("Ti")));

        onView(withId(R.id.edit_quiz_title)).perform((typeText("tle")));
        Espresso.closeSoftKeyboard();
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
        onView(withId(R.id.edit_quiz_title)).perform((typeText("tle")));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.button_start_editing)).perform(click());
        intended(
                allOf(
                        hasComponent(EditQuizActivity.class.getName()),
                        hasExtra(equalTo(QUIZ_BUILDER), instanceOf(Quiz.Builder.class)),
                        hasExtra(equalTo(STRING_POOL), instanceOf(StringPool.class))));
    }

    @Test
    public void treasureHuntCheckbox() {
        onView(withId(R.id.treasure_hunt_checkbox));
        onView(withId(R.id.treasure_hunt_checkbox)).perform(click());
        onView(withId(R.id.treasure_hunt_checkbox)).check(matches(isChecked()));
        onView(withId(R.id.treasure_hunt_checkbox)).perform(click());
    }
}
