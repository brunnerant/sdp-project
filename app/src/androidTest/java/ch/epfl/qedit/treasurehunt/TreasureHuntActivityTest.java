package ch.epfl.qedit.treasurehunt;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isA;
import static org.hamcrest.Matchers.not;

import android.content.Intent;
import android.location.Location;
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
        AnswerFormat format =
                MatrixFormat.singleField(
                        MatrixFormat.Field.textField("", MatrixFormat.Field.NO_LIMIT));
        Location location = new Location("");
        location.setLongitude(0);
        location.setLatitude(0);
        Question question = new Question("title", "text", format, location, 100);
        Quiz quiz = new Quiz("title", Collections.nCopies(numQuestions, question), true);

        bundle.putSerializable(TreasureHuntActivity.QUIZ_ID, quiz);
        intent.putExtras(bundle);

        // Finally, we launch the activity
        testRule.launchActivity(intent);
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

    private void checkIntent() {
        intended(
                allOf(
                        hasComponent(QuestionLocatorActivity.class.getName()),
                        hasExtra(
                                equalTo(QuestionLocatorActivity.QUESTION_LOCATION),
                                isA(Location.class)),
                        hasExtra(
                                equalTo(QuestionLocatorActivity.QUESTION_RADIUS),
                                isA(double.class))));
    }

    @Test
    public void testLaunchQuestionLocator() {
        init(1);

        onView(withText(R.string.treasure_hunt_start)).perform(click());
        checkIntent();
    }

    // Finds the question and returns to the treasure hunt activity
    private void findQuestion() {
        // We move to the next question
        locService.setLocation(0, 0);

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
    }
}
