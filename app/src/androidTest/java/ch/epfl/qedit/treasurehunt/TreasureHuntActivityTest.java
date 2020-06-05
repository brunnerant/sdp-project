package ch.epfl.qedit.treasurehunt;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.times;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.qedit.view.home.HomeQuizListFragment.QUIZ_ID;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.Intent;
import android.os.Bundle;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import ch.epfl.qedit.R;
import ch.epfl.qedit.backend.location.LocServiceFactory;
import ch.epfl.qedit.backend.location.MockLocService;
import ch.epfl.qedit.backend.permission.MockPermManager;
import ch.epfl.qedit.backend.permission.PermManagerFactory;
import ch.epfl.qedit.model.Question;
import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.model.answer.AnswerFormat;
import ch.epfl.qedit.model.answer.MatrixFormat;
import ch.epfl.qedit.view.treasurehunt.QuestionLocatorActivity;
import ch.epfl.qedit.view.treasurehunt.TreasureHuntActivity;
import java.util.Collections;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4ClassRunner.class)
public class TreasureHuntActivityTest {
    @Rule
    public final IntentsTestRule<TreasureHuntActivity> testRule =
            new IntentsTestRule<>(TreasureHuntActivity.class, false, false);

    private MockLocService locService;

    public void init(int numQuestions) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();

        // We create a mock permission service, otherwise the UI test might fail because
        // android shows a permission popup
        PermManagerFactory.setInstance(new MockPermManager());

        // We also create a mock location service
        LocServiceFactory.setInstance(
                context -> {
                    locService = new MockLocService(context);
                    return locService;
                });

        // We prepare a mock quiz to test the activity
        AnswerFormat format = MatrixFormat.singleField(MatrixFormat.Field.textField(""));
        Question question = new Question("title", "text", format, 42, 43, 100);
        Quiz quiz = new Quiz("title", Collections.nCopies(numQuestions, question), true);

        bundle.putSerializable(QUIZ_ID, quiz);
        intent.putExtras(bundle);

        // Finally, we launch the activity
        testRule.launchActivity(intent);
    }

    @After
    public void cleanup() {
        testRule.finishActivity();
    }

    @Test
    public void testWelcomePage() {
        init(1);

        // The welcome page should be displayed
        onView(withText(R.string.treasure_hunt_explanation)).check(matches(isDisplayed()));
        onView(withText(R.string.treasure_hunt_start)).check(matches(isDisplayed()));

        // But the question UI should not be displayed
        onView(withId(R.id.question_done)).check(doesNotExist());
        onView(withId(R.id.treasure_hunt_question)).check(matches(not(isDisplayed())));
    }

    private void checkIntents(int n) {
        intended(
                allOf(
                        hasComponent(QuestionLocatorActivity.class.getName()),
                        hasExtra(
                                equalTo(QuestionLocatorActivity.QUESTION_LONGITUDE), equalTo(42.0)),
                        hasExtra(equalTo(QuestionLocatorActivity.QUESTION_LATITUDE), equalTo(43.0)),
                        hasExtra(equalTo(QuestionLocatorActivity.QUESTION_RADIUS), equalTo(100.0))),
                times(n));
    }

    @Test
    public void testLaunchQuestionLocator() {
        init(1);

        onView(withText(R.string.treasure_hunt_start)).perform(click());
        checkIntents(1);
    }

    // Finds the question and returns to the treasure hunt activity
    private void findQuestion() {
        // We move to the next question
        locService.setLocation(42, 43);

        // And ask to answer the question
        onView(withId(R.id.question_locator_button)).perform(click());
    }

    @Test
    public void testQuestionIsDisplayedOnceQuestionFound() {
        init(2);

        // We locate the next question, and come back
        onView(withText(R.string.treasure_hunt_start)).perform(click());
        findQuestion();

        // The helper view should be hidden
        onView(withId(R.id.treasure_hunt_helper_view)).check(matches(not(isDisplayed())));

        // But the question UI should be displayed
        onView(withId(R.id.question_done)).check(matches(isDisplayed()));
        onView(withId(R.id.treasure_hunt_question)).check(matches(isDisplayed()));

        // Clicking on the "done" button should show the confirmation and go to the next question
        onView(withId(R.id.question_done)).perform(click());
        onView(withId(android.R.id.button1)).perform(click());
        checkIntents(2);
    }

    @Test
    public void testLastQuestionEndsTreasureHunt() {
        init(1);

        // We locate the next question, and come back
        onView(withText(R.string.treasure_hunt_start)).perform(click());
        findQuestion();

        // Clicking on "done" for the last question should finish the activity
        assertFalse(testRule.getActivity().isFinishing());
        onView(withId(R.id.question_done)).perform(click());
        onView(withId(android.R.id.button1)).perform(click());
        assertTrue(testRule.getActivity().isFinishing());
    }
}
