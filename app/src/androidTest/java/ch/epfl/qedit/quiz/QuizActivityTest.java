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
import static ch.epfl.qedit.model.StringPool.TITLE_ID;
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
import ch.epfl.qedit.model.StringPool;
import ch.epfl.qedit.model.answer.AnswerModel;
import ch.epfl.qedit.model.answer.MatrixFormat;
import ch.epfl.qedit.model.answer.MatrixModel;
import ch.epfl.qedit.view.answer.MatrixFragment;
import ch.epfl.qedit.view.quiz.QuizActivity;
import ch.epfl.qedit.viewmodel.QuizViewModel;
import java.util.HashMap;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4ClassRunner.class)
public class QuizActivityTest {
    private Quiz quiz;
    private StringPool stringPool;
    private QuizViewModel model;
    private final Integer zero = 0;
    private final String answer1 = "1234";

    @Rule
    public final IntentsTestRule<QuizActivity> testRule =
            new IntentsTestRule<>(QuizActivity.class, false, false);

    public void launchActivity() {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();

        initializeStringPoolAndQuiz();

        bundle.putSerializable(QUIZ_ID, quiz);
        intent.putExtras(bundle);

        testRule.launchActivity(intent);

        model = new ViewModelProvider(testRule.getActivity()).get(QuizViewModel.class);
        model.setQuiz(quiz.instantiateLanguage(stringPool));

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

    public void finishActivity() {
        testRule.finishActivity();
    }

    private void initializeStringPoolAndQuiz() {
        stringPool = new StringPool();
        stringPool.update(TITLE_ID, "Title");

        MatrixFormat.Field field = MatrixFormat.Field.numericField(false, false, "0");
        Quiz.Builder builder = new Quiz.Builder();
        builder.append(
                        new Question(
                                stringPool.add("Bananas"),
                                stringPool.add("How many?"),
                                MatrixFormat.singleField(field)))
                .append(
                        new Question(
                                stringPool.add("Vector"),
                                stringPool.add("Fill this Vector!"),
                                MatrixFormat.uniform(7, 1, field)));

        quiz = builder.build().instantiateLanguage(stringPool);
    }

    @Test
    public void testOnCreateState() {
        launchActivity();
        Integer question = model.getFocusedQuestion().getValue();
        Assert.assertNull(question);
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
    public void testValidateClicked() {
        launchActivity();
        onView(withId(R.id.validate)).perform(click());
        finishActivity();
    }

    @Test
    public void testDoneNoCLicked() {
        launchActivity();
        onView(withId(R.id.validate)).perform(click());
        onView(withId(android.R.id.button2)).perform(click());
        finishActivity();
    }

    @Test
    public void testDoneYesClicked() {
        launchActivity();
        onView(withId(R.id.validate)).perform(click());
        onView(withId(android.R.id.button1)).perform(click());
        onView(withText("number of good answers = 0"))
                .inRoot(
                        withDecorView(
                                Matchers.not(
                                        is(testRule.getActivity().getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
        finishActivity();
    }

    @Test
    public void quizOverviewIsDisplayed() {
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
    public void testQuestionIsNotDisplayed() {
        launchActivity();
        onView(withId(R.id.question)).check(matches(isDisplayed()));
        finishActivity();
    }

    @Test
    public void testQuizOverviewOnClickDisplayedAgain() {
        launchActivity();
        onView(withId(R.id.quiz_overview_container)).perform(click()).perform(click());
        onView(withId(R.id.quiz_overview_container)).check(matches(isDisplayed()));
        finishActivity();
    }

    @Test
    public void testAnswersAreRestored() {
        launchActivity();
        onView(withId(R.id.next)).perform(click());
        onView(withId(R.id.next)).perform(click());

        int id =
                ((MatrixFragment)
                                testRule.getActivity()
                                        .getSupportFragmentManager()
                                        .findFragmentByTag(FRAGMENT_TAG))
                        .getId(0, 0);

        String answer2 = "5678";
        onView(withId(id)).perform(typeText(answer2)).perform(closeSoftKeyboard());
        onView(withId(R.id.previous)).perform(click());
        onView(withId(R.id.next)).perform(click());

        id =
                ((MatrixFragment)
                                testRule.getActivity()
                                        .getSupportFragmentManager()
                                        .findFragmentByTag(FRAGMENT_TAG))
                        .getId(0, 0);

        onView(withId(id)).check(matches(withText(answer2)));
        finishActivity();
    }

    @Test
    public void testAnswerIsLoadedFromQuizViewModel() {
        launchActivity();
        onView(withId(R.id.next)).perform(click());

        MatrixFragment matrixFragment =
                (MatrixFragment)
                        testRule.getActivity()
                                .getSupportFragmentManager()
                                .findFragmentByTag(FRAGMENT_TAG);
        int id = matrixFragment.getId(0, 0);
        onView(withId(id)).check(matches(withText(answer1)));
        finishActivity();
    }
}
