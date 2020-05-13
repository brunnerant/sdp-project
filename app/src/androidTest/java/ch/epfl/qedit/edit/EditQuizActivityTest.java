package ch.epfl.qedit.edit;

import static android.app.Activity.RESULT_OK;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.ActivityResultMatchers.hasResultCode;
import static androidx.test.espresso.contrib.ActivityResultMatchers.hasResultData;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.qedit.util.Util.createMockQuiz;
import static ch.epfl.qedit.view.edit.EditSettingsActivity.QUIZ_BUILDER;
import static ch.epfl.qedit.view.home.HomeQuizListFragment.QUIZ_ID;
import static ch.epfl.qedit.view.home.HomeQuizListFragment.STRING_POOL;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNot.not;

import android.content.Intent;
import android.os.Bundle;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import ch.epfl.qedit.R;
import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.model.StringPool;
import ch.epfl.qedit.view.edit.EditQuizActivity;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class EditQuizActivityTest {
    private Quiz quiz;

    @Rule
    public final IntentsTestRule<EditQuizActivity> testRule =
            new IntentsTestRule<>(EditQuizActivity.class, false, false);

    @Before
    public void setUp() {
        quiz = createMockQuiz("Test");
        Quiz.Builder quizBuilder = new Quiz.Builder(quiz);
        StringPool stringPool = new StringPool();

        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable(QUIZ_BUILDER, quizBuilder);
        bundle.putSerializable(STRING_POOL, stringPool);
        intent.putExtras(bundle);

        testRule.launchActivity(intent);
    }

    @After
    public void cleanUp() {
        testRule.finishActivity();
    }

    @Test
    public void testThatFragmentsAreDisplayed() {
        onView(withId(R.id.quiz_overview_container)).check(matches(isDisplayed()));
        onView(withId(R.id.question_details_container)).check(matches(isDisplayed()));
    }

    @Test
    public void testMenu() {
        onView(withId(R.id.overview)).perform(click());
        onView(withId(R.id.quiz_overview_container)).check(matches(not(isDisplayed())));
        onView(withId(R.id.overview)).perform(click());

        onView(withId(R.id.previous)).perform(click());
        onView(withId(R.id.previous)).perform(click());
        onView(withId(R.id.previous)).perform(click());
        onView(withId(R.id.next)).perform(click());

        onView(withId(R.id.question_title))
                .check(matches(withText(quiz.getQuestions().get(1).getTitle())));

        onView(withId(R.id.quiz_overview_container)).check(matches(isDisplayed()));
        onView(withId(R.id.question_details_container)).check(matches(isDisplayed()));
    }

    @Test
    public void testReturnResult() {
        onView(withId(R.id.done)).perform(click());

        assertThat(testRule.getActivityResult(), hasResultCode(RESULT_OK));
        assertThat(
                testRule.getActivityResult(),
                hasResultData(IntentMatchers.hasExtraWithKey(QUIZ_ID)));
        assertThat(
                testRule.getActivityResult(),
                hasResultData(IntentMatchers.hasExtraWithKey(STRING_POOL)));
    }
}
