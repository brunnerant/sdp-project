package ch.epfl.qedit.quiz;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.core.IsNot.not;

import android.content.Intent;
import android.os.Bundle;
import androidx.lifecycle.ViewModelProvider;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import ch.epfl.qedit.R;
import ch.epfl.qedit.view.home.HomeQuizListFragment;
import ch.epfl.qedit.view.quiz.QuizActivity;

import ch.epfl.qedit.viewmodel.QuizViewModel;
import org.junit.Assert;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4ClassRunner.class)
public class QuizActivityTest {
    private QuizViewModel model;
    private Integer zero = 0;

    @Rule
    public final IntentsTestRule<QuizActivity> testRule =
            new IntentsTestRule<>(QuizActivity.class, false, false);

    public void launchActivity() {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable(HomeQuizListFragment.QUIZID, "quiz0");
        intent.putExtras(bundle);
        testRule.launchActivity(intent);
        model = new ViewModelProvider(testRule.getActivity()).get(QuizViewModel.class);

        while (!model.getStatus().getValue().equals(QuizViewModel.Status.Loaded)) {}
    }

    public void finishActivity() {
        testRule.finishActivity();
    }

    @Test
    public void testOnCreateState() {
        launchActivity();
        Integer question = model.getFocusedQuestion().getValue();
        Assert.assertEquals(question, null);
        finishActivity();
    }

    @Test
    public void clickPreviousNull() {
        launchActivity();
        onView(withId(R.id.previous)).perform(click());
        Assert.assertEquals(model.getFocusedQuestion().getValue(), zero);
        finishActivity();
    }

    @Test
    public void cantGoUnder0() {
        launchActivity();
        onView(withId(R.id.previous)).perform(click());
        onView(withId(R.id.previous)).perform(click());
        Assert.assertEquals(model.getFocusedQuestion().getValue(), zero);
        finishActivity();
    }

    @Test
    public void cantGoAboveQuizSize() {
        launchActivity();

        for (int i = 0; i < model.getQuiz().getValue().getQuestions().size(); ++i) {
            onView(withId(R.id.next)).perform(click());
        }

        onView(withId(R.id.next)).perform(click());
        Integer index = model.getQuiz().getValue().getQuestions().size() - 1;
        Assert.assertEquals(model.getFocusedQuestion().getValue(), index);

        finishActivity();
    }

    @Test
    public void quizOverviewDisplayed() {
        launchActivity();
        onView(withId(R.id.quiz_overview_container)).check(matches(isDisplayed()));
        finishActivity();
    }

    @Test
    public void quizOverviewDisappears() {
        launchActivity();
        onView(withId(R.id.overview)).perform(click());
        onView(withId(R.id.quiz_overview_container)).check(matches(not(isDisplayed())));
        finishActivity();
    }

    @Test
    public void testQuizOverviewIsDisplayed() {
        launchActivity();
        onView(ViewMatchers.withId(R.id.quiz_overview_container)).check(matches(isDisplayed()));
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
