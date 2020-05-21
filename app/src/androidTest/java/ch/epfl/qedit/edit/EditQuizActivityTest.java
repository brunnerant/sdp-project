package ch.epfl.qedit.edit;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.ActivityResultMatchers.hasResultCode;
import static androidx.test.espresso.contrib.ActivityResultMatchers.hasResultData;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.qedit.util.Util.createTestQuiz;
import static ch.epfl.qedit.util.Util.createTestStringPool;
import static ch.epfl.qedit.util.Util.onDialog;
import static ch.epfl.qedit.view.edit.EditQuizSettingsDialog.QUIZ_BUILDER;
import static ch.epfl.qedit.view.home.HomeQuizListFragment.QUIZ_ID;
import static ch.epfl.qedit.view.home.HomeQuizListFragment.STRING_POOL;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertFalse;

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

public class EditQuizActivityTest extends EditTest {
    private Quiz quiz;
    private StringPool stringPool;

    @Rule
    public final IntentsTestRule<EditQuizActivity> testRule =
            new IntentsTestRule<>(EditQuizActivity.class, false, false);

    @Before
    public void setUp() {
        quiz = createTestQuiz();
        Quiz.Builder quizBuilder = new Quiz.Builder(quiz);
        stringPool = createTestStringPool("Title");

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
    public void testBasicMenuOperations() {
        onView(withId(R.id.overview)).perform(click());
        onView(withId(R.id.quiz_overview_container)).check(matches(not(isDisplayed())));
        onView(withId(R.id.overview)).perform(click());

        onView(withId(R.id.previous)).perform(click());
        onView(withId(R.id.previous)).perform(click());
        onView(withId(R.id.previous)).perform(click());
        onView(withId(R.id.next)).perform(click());

        onView(withId(R.id.question_title))
                .check(matches(withText(stringPool.get(quiz.getQuestions().get(1).getTitle()))));

        onView(withId(R.id.quiz_overview_container)).check(matches(isDisplayed()));
        onView(withId(R.id.question_details_container)).check(matches(isDisplayed()));
    }

    @Test
    public void testReturnEditedResult() {
        onView(withId(R.id.done)).perform(click());
        onView(withText(testRule.getActivity().getString(R.string.warning_done_edition)))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

        onDialog(android.R.id.button2).perform(click());
        assertFalse(testRule.getActivity().isFinishing());

        onView(withId(R.id.done)).perform(click());
        onDialog(android.R.id.button1).perform(click());
        assertThat(testRule.getActivityResult(), hasResultCode(RESULT_OK));
        assertThat(
                testRule.getActivityResult(),
                hasResultData(IntentMatchers.hasExtraWithKey(QUIZ_ID)));
        assertThat(
                testRule.getActivityResult(),
                hasResultData(IntentMatchers.hasExtraWithKey(STRING_POOL)));
        assertTrue(testRule.getActivity().isFinishing());
    }

    @Test
    public void testCancelEdition() {
        onView(withId(R.id.exit)).perform(click());
        onView(withText(testRule.getActivity().getString(R.string.warning_exit_edition)))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

        onDialog(android.R.id.button2).perform(click());
        assertFalse(testRule.getActivity().isFinishing());

        onView(withId(R.id.exit)).perform(click());
        onDialog(android.R.id.button1).perform(click());
        assertThat(testRule.getActivityResult(), hasResultCode(RESULT_CANCELED));
        assertTrue(testRule.getActivity().isFinishing());
    }

    @Test
    public void testSaveEmptyQuiz() {
        emptyQuizList(quiz);

        onView(withId(R.id.done)).perform(click());
        onView(withText(testRule.getActivity().getString(R.string.warning_save_empty_quiz)))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

        onDialog(android.R.id.button2).perform(click());
        onView(withText(testRule.getActivity().getString(R.string.empty_question_list_hint_text)))
                .check(matches(isDisplayed()));
    }
}
