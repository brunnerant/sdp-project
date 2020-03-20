package ch.epfl.qedit;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Intent;
import android.os.Bundle;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import ch.epfl.qedit.view.home.HomeQuizListFragment;
import ch.epfl.qedit.view.quiz.QuizActivity;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4ClassRunner.class)
public class QuizActivityTest {
    @Rule
    public final IntentsTestRule<QuizActivity> testRule =
            new IntentsTestRule<>(QuizActivity.class, false, false);

    public void launchActivity() {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable(HomeQuizListFragment.QUIZID, "quiz0");
        intent.putExtras(bundle);
        testRule.launchActivity(intent);
    }

    public void finishActivity() {
        testRule.finishActivity();
    }

    @Test
    public void testQuizOverviewIsDisplayed() {
        launchActivity();
        onView(withId(R.id.quiz_overview_container)).check(matches(isDisplayed()));
        finishActivity();
    }

    @Test
    public void testQuizOverviewOnClick() {
        launchActivity();
        onView(withId(R.id.quiz_overview_container)).perform(click());
        finishActivity();
    }

    @Test
    public void testQuestionIsNotDisplayed() {
        launchActivity();
        onView(withId(R.id.question)).check(matches(isDisplayed()));
        finishActivity();
    }
}
