package ch.epfl.qedit.home;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.qedit.util.Util.onDialog;
import static ch.epfl.qedit.view.edit.EditQuizSettingsDialog.QUIZ_BUILDER;
import static ch.epfl.qedit.view.home.HomeQuizListFragment.STRING_POOL;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.IsNot.not;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.intent.Intents;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import ch.epfl.qedit.R;
import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.model.StringPool;
import ch.epfl.qedit.view.edit.EditQuizActivity;
import ch.epfl.qedit.view.home.HomeQuizListFragment;
import com.android21buttons.fragmenttestrule.FragmentTestRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

// Note that this test doesn't test the behaviour of the recycler is already tested
// in another test
@RunWith(AndroidJUnit4ClassRunner.class)
public class HomeQuizListFragmentTest extends HomeFragmentsTestUsingDB {
    @Rule
    public final FragmentTestRule<?, HomeQuizListFragment> testRule =
            FragmentTestRule.create(HomeQuizListFragment.class, false, false);

    public HomeQuizListFragmentTest() {
        super(R.id.home_quiz_list);
    }

    @Before
    public void setup() {
        Intents.init();
        setup(testRule, new HomeQuizListFragment());
    }

    @After
    public void cleanup() {
        Intents.release();
        cleanup(testRule);
    }

    @Test
    public void testQuizListIsProperlyLoaded() {
        itemView(0, android.R.id.text1).check(matches(withText("I am a Mock Quiz!")));
    }

    @Test
    public void testDeleteItem() {
        item(0).perform(click());
        itemView(0, R.id.delete_button).perform(click());

        // Check that the confirmation dialog is displayed
        onView(withText(testRule.getActivity().getString(R.string.warning_delete_quiz)))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

        // Cancel, and see if the item is still present
        onView(withId(android.R.id.button2)).perform(click());
        item(0).check(matches(isDisplayed()));

        // Now delete for real and see if it was deleted
        itemView(0, R.id.delete_button).perform(click());
        onView(withId(android.R.id.button1)).perform(click());
        onView(withText("I am a Mock Quiz!")).check(doesNotExist());
    }

    // Edition of a existing quiz dialog tests

    @Test
    public void testClickOnEditLaunchesDialogModify() {
        item(0).perform(click());
        itemView(0, R.id.edit_button).perform(click());

        onView(withText(testRule.getActivity().getString(R.string.edit_dialog_title_settings)))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));
        onDialog(R.id.edit_quiz_title).check(matches(isDisplayed()));
        onDialog(R.id.treasure_hunt_checkbox).check(matches(not(isDisplayed())));
        onDialog(R.id.edit_language_selection).check(matches(not(isDisplayed())));
    }

    @Test
    public void testEnterTitle() {
        item(0).perform(click());
        itemView(0, R.id.edit_button).perform(click());

        onDialog(R.id.edit_quiz_title).check(matches(withText("I am a Mock Quiz!")));

        onDialog(R.id.edit_quiz_title).perform(clearText());
        onDialog(R.id.edit_quiz_title).perform(typeText("New Title"));
        Espresso.closeSoftKeyboard();
        onDialog(R.id.edit_quiz_title).check(matches(withText("New Title")));
    }

    @Test
    public void testStartEditing() {
        item(0).perform(click());
        itemView(0, R.id.edit_button).perform(click());

        onDialog(android.R.id.button1).perform(click());
        intended(
                allOf(
                        hasComponent(EditQuizActivity.class.getName()),
                        hasExtra(equalTo(STRING_POOL), instanceOf(StringPool.class)),
                        hasExtra(equalTo(QUIZ_BUILDER), instanceOf(Quiz.Builder.class))));
    }
}
