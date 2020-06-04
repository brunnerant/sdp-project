package ch.epfl.qedit.quiz;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.qedit.model.StringPool.TITLE_ID;
import static ch.epfl.qedit.view.home.HomeQuizListFragment.QUIZ_ID;
import static ch.epfl.qedit.view.login.Util.USER;
import static ch.epfl.qedit.view.quiz.QuizActivity.CORRECTION;
import static ch.epfl.qedit.view.quiz.QuizActivity.GOOD_ANSWERS;
import static org.hamcrest.Matchers.allOf;

import android.content.Intent;
import android.os.Bundle;
import androidx.lifecycle.ViewModelProvider;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import ch.epfl.qedit.R;
import ch.epfl.qedit.model.Question;
import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.model.StringPool;
import ch.epfl.qedit.model.User;
import ch.epfl.qedit.model.answer.MatrixFormat;
import ch.epfl.qedit.view.home.HomeActivity;
import ch.epfl.qedit.view.quiz.QuizActivity;
import ch.epfl.qedit.viewmodel.QuizViewModel;
import java.util.ArrayList;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4ClassRunner.class)
public class QuizCorrectionActivityTest {
    private Quiz quiz;
    private StringPool stringPool;
    private QuizViewModel model;
    private final Integer zero = 0;
    private final String answer1 = "1234";
    private ArrayList<Integer> correctedQuestions;

    @Rule
    public final IntentsTestRule<QuizActivity> testRule =
            new IntentsTestRule<>(QuizActivity.class, false, false);

    public void launchActivity() {
        User user = new User("François", "Ferdinand");
        correctedQuestions = new ArrayList<>();
        correctedQuestions.add(0, 1);
        correctedQuestions.add(1, 0);

        Intent intent = new Intent();
        Bundle bundle = new Bundle();

        initializeStringPoolAndQuiz();

        bundle.putSerializable(QUIZ_ID, quiz);
        bundle.putIntegerArrayList(GOOD_ANSWERS, correctedQuestions);
        bundle.putSerializable(USER, user);
        bundle.putBoolean(CORRECTION, true);
        intent.putExtras(bundle);

        testRule.launchActivity(intent);

        model = new ViewModelProvider(testRule.getActivity()).get(QuizViewModel.class);
        model.setQuiz(quiz.instantiateLanguage(stringPool));

        /*final MatrixModel matrixModel = new MatrixModel(1, 1);
        matrixModel.updateAnswer(0, 0, answer1);
        model.getAnswers()
                .postValue(
                        new HashMap<Integer, AnswerModel>() {
                            {
                                put(0, matrixModel);
                            }
                        });*/

    }

    public void finishActivity() {
        testRule.finishActivity();
    }

    private void initializeStringPoolAndQuiz() {
        stringPool = new StringPool();
        stringPool.update(TITLE_ID, "CorrectionTest");

        MatrixFormat.Field field = MatrixFormat.Field.preFilledField("42");
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
    public void correctionFragmentDisplayed() {
        launchActivity();
        onView(withId(R.id.correction_details_container)).check(matches(isDisplayed()));
        finishActivity();
    }

    @Test
    public void clickValidate() {
        launchActivity();
        onView(withId(R.id.validate)).perform(click());
        onView(withId(android.R.id.button1)).perform(click());
        intended(allOf(hasComponent(HomeActivity.class.getName())));
        finishActivity();
    }

    @Test
    public void textViewsContent() {
        launchActivity();
        onView(withId(R.id.correct_ratio)).check(matches(withText("Correct answers :1/2 (50.0%)")));
        onView(withId(R.id.correction_score)).check(matches(withText("+1")));
        onView(withId(R.id.correction_success)).check(matches(withText("+0")));
        onView(withId(R.id.correction_attempts)).check(matches(withText("+1")));
        finishActivity();
    }
}
