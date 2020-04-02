package ch.epfl.qedit.home;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.qedit.view.home.HomeQuizListFragment.QUIZ_ID;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;

import androidx.test.espresso.intent.Intents;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import ch.epfl.qedit.R;
import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.view.home.HomeQuizListFragment;
import ch.epfl.qedit.view.quiz.QuizActivity;
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
        itemView(0, android.R.id.text1).check(matches(withText("Qualification EPFL")));
    }

    @Test
    public void testDeleteItem() {
        item(0).perform(click());
        itemView(0, R.id.delete_button).perform(click());

        // Check that the confirmation dialog is displayed
        onView(withText(testRule.getActivity().getString(R.string.warning_delete)))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

        // Cancel, and see if the item is still present
        onView(withId(android.R.id.button2)).perform(click());
        item(0).check(matches(isDisplayed()));

        // Now delete for real and see if it was deleted
        itemView(0, R.id.delete_button).perform(click());
        onView(withId(android.R.id.button1)).perform(click());
        onView(withText("Qualification EPFL")).check(doesNotExist());
    }

    @Test
    public void testClickOnQuizLaunchesQuizActivity() {
        item(0).perform(click());
        itemView(0, R.id.edit_button).perform(click());
        intended(
                allOf(
                        hasComponent(QuizActivity.class.getName()),
                        hasExtra(equalTo(QUIZ_ID), instanceOf(Quiz.class))));
    }
}
