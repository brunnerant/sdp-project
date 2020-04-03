package ch.epfl.qedit.quiz;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.qedit.view.home.HomeQuizListFragment.QUIZ_ID;
import static ch.epfl.qedit.view.quiz.QuestionFragment.FRAGMENT_TAG;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;

import android.content.Intent;
import android.os.Bundle;
import androidx.lifecycle.ViewModelProvider;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import ch.epfl.qedit.R;
import ch.epfl.qedit.model.Question;
import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.model.answer.AnswerModel;
import ch.epfl.qedit.model.answer.MatrixModel;
import ch.epfl.qedit.view.answer.MatrixFragment;
import ch.epfl.qedit.view.quiz.QuizActivity;
import ch.epfl.qedit.viewmodel.QuizViewModel;
import java.util.Arrays;
import java.util.HashMap;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4ClassRunner.class)
public class QuizActivityTest {
    private QuizViewModel model;
    private final Integer zero = 0;
    private final String answer1 = "1234";

    @Rule
    public final IntentsTestRule<QuizActivity> testRule =
            new IntentsTestRule<>(QuizActivity.class, false, false);

    @Before
    public void launchActivity() {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        Quiz quiz =
                new Quiz(
                        "Title",
                        Arrays.asList(
                                new Question("Banane", "How many?", "matrix1x1"),
                                new Question("Vector", "Fill this Vector!", "matrix7x1")));
        bundle.putSerializable(QUIZ_ID, quiz);
        intent.putExtras(bundle);

        testRule.launchActivity(intent);

        model = new ViewModelProvider(testRule.getActivity()).get(QuizViewModel.class);
        model.setQuiz(quiz);

        final MatrixModel matrixModel = new MatrixModel(1, 1);
        matrixModel.updateAnswer(0, 0, answer1);
        model.getAnswers()
                .postValue(
                        new HashMap<Integer, AnswerModel>() {
                            {
                                put(0, matrixModel);
                            }
                        });
    }

    @After
    public void finishActivity() {
        testRule.finishActivity();
    }

    @Test
    public void testOnCreateState() {
        Integer question = model.getFocusedQuestion().getValue();
        Assert.assertNull(question);
    }

    @Test
    public void clickPreviousNull() {
        onView(withId(R.id.previous)).perform(click());
        Assert.assertEquals(model.getFocusedQuestion().getValue(), zero);
    }

    @Test
    public void cantGoUnder0() {
        onView(withId(R.id.previous)).perform(click());
        onView(withId(R.id.previous)).perform(click());
        Assert.assertEquals(model.getFocusedQuestion().getValue(), zero);
    }

    @Test
    public void cantGoAboveQuizSize() {
        for (int i = 0; i < model.getQuiz().getQuestions().size(); ++i) {
            onView(withId(R.id.next)).perform(click());
        }

        onView(withId(R.id.next)).perform(click());
        Integer index = model.getQuiz().getQuestions().size() - 1;
        Assert.assertEquals(model.getFocusedQuestion().getValue(), index);
    }

    @Test
    public void testUpArrowIsClicked() {
        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click());
        assertTrue(testRule.getActivity().isFinishing());
    }

    @Test
    public void testTimeIsClicked() {
        onView(withId(R.id.time)).perform(click());
        onView(withText("Unimplemented Feature"))
                .inRoot(
                        withDecorView(
                                Matchers.not(
                                        is(testRule.getActivity().getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
    }

    @Test
    public void quizOverviewIsDisplayed() {
        onView(withId(R.id.quiz_overview_container)).check(matches(isDisplayed()));
    }

    @Test
    public void quizOverviewDisappears() {
        onView(withId(R.id.overview)).perform(click());
        onView(withId(R.id.quiz_overview_container)).check(matches(not(isDisplayed())));
    }

    @Test
    public void testQuestionIsNotDisplayed() {
        onView(withId(R.id.question)).check(matches(isDisplayed()));
    }

    @Test
    public void testQuizOverviewOnClickDisplayedAgain() {
        onView(withId(R.id.quiz_overview_container)).perform(click()).perform(click());
        onView(withId(R.id.quiz_overview_container)).check(matches(isDisplayed()));
    }

    @Test
    public void testAnswersAreRestored() {
        onView(withId(R.id.next)).perform(click());
        onView(withId(R.id.next)).perform(click());

        MatrixFragment matrixFragment =
                (MatrixFragment)
                        testRule.getActivity()
                                .getSupportFragmentManager()
                                .findFragmentByTag(FRAGMENT_TAG);
        int id = matrixFragment.getId(0, 0);

        String answer2 = "5678";
        onView(withId(id)).perform((typeText(answer2))).perform(closeSoftKeyboard());
        onView(withId(R.id.previous)).perform(click());
        onView(withId(R.id.next)).perform(click());

        matrixFragment =
                (MatrixFragment)
                        testRule.getActivity()
                                .getSupportFragmentManager()
                                .findFragmentByTag(FRAGMENT_TAG);
        id = matrixFragment.getId(0, 0);

        onView(withId(id)).check(matches(withText(answer2)));
    }

    @Test
    public void testAnswerIsLoadedFromQuizViewModel() {
        onView(withId(R.id.next)).perform(click());

        MatrixFragment matrixFragment =
                (MatrixFragment)
                        testRule.getActivity()
                                .getSupportFragmentManager()
                                .findFragmentByTag(FRAGMENT_TAG);
        int id = matrixFragment.getId(0, 0);
        onView(withId(id)).check(matches(withText(answer1)));
    }
}
