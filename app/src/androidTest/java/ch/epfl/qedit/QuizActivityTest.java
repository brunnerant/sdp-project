package ch.epfl.qedit;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;

import android.content.Intent;
import android.os.Bundle;
import androidx.lifecycle.ViewModelProvider;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import ch.epfl.qedit.model.Question;
import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.view.home.HomeQuizListFragment;
import ch.epfl.qedit.view.quiz.QuizActivity;
import ch.epfl.qedit.viewmodel.QuizViewModel;
import java.util.Arrays;
import org.hamcrest.Matchers;
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
        Quiz quiz =
                new Quiz(
                        "Title",
                        Arrays.asList(
                                new Question("Banane", "How many?", "matrix1x1"),
                                new Question("Vector", "Fill this Vector!", "matrix7x1")));
        bundle.putSerializable(HomeQuizListFragment.QUIZID, quiz);
        intent.putExtras(bundle);
        testRule.launchActivity(intent);
        model = new ViewModelProvider(testRule.getActivity()).get(QuizViewModel.class);
        model.setQuiz(quiz);
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

        for (int i = 0; i < model.getQuiz().getQuestions().size(); ++i) {
            onView(withId(R.id.next)).perform(click());
        }

        onView(withId(R.id.next)).perform(click());
        Integer index = model.getQuiz().getQuestions().size() - 1;
        Assert.assertEquals(model.getFocusedQuestion().getValue(), index);

        finishActivity();
    }

    @Test
    public void testUpArrowIsClicked() {
        launchActivity();
        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click());
        assertTrue(testRule.getActivity().isFinishing());
        finishActivity();
    }

    @Test
    public void testTimeIsClicked() {
        launchActivity();
        onView(withId(R.id.time)).perform(click());
        onView(withText("Unimplemented Feature"))
                .inRoot(
                        withDecorView(
                                Matchers.not(
                                        is(testRule.getActivity().getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
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
}
