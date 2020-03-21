package ch.epfl.qedit.answer;

import android.content.Intent;
import android.os.Bundle;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import ch.epfl.qedit.backend.database.DatabaseFactory;
import ch.epfl.qedit.backend.database.MockDBService;
import ch.epfl.qedit.model.MatrixFormat;
import ch.epfl.qedit.view.answer.MatrixFragment;
import ch.epfl.qedit.view.quiz.QuizActivity;
import ch.epfl.qedit.viewmodel.QuizViewModel;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4ClassRunner.class)
public class MatrixFragmentTest {

    private IdlingResource idlingResource;
    private QuizViewModel model;
    private MatrixFragment matrixFragment;

    @Rule
    public final IntentsTestRule<QuizActivity> testRule =
            new IntentsTestRule<>(QuizActivity.class, false, false);

    @Before
    public void init() {
        MockDBService dbService = new MockDBService();
        idlingResource = dbService.getIdlingResource();
        IdlingRegistry.getInstance().register(idlingResource);
        DatabaseFactory.setInstance(dbService);
        // launchFragment();
    }

    @After
    public void cleanup() {
        testRule.finishActivity();
        IdlingRegistry.getInstance().unregister(idlingResource);
    }

    public void launchFragment() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(MatrixFragment.MATRIXID, MatrixFormat.createMatrix3x3());
        // matrixFragment.setArguments(bundle);
        Intent intent = new Intent();
        intent.putExtras(bundle);
        testRule.launchActivity(intent);
    }

    @Test
    public void testFragmentIsEmptyByDefault() {

        // testRule.getActivity().getSupportFragmentManager().getFragments().get(R.id.answersTable);
        // onView(withId(R.id.answersTable));
        // onView(withId(R.id.)).check(matches(withText("000")));
        // onView(withId(R.id.question_display)).check(matches(withText("")));
        // onView(withId(R.id.answer_fragment)).check(doesNotExist());
    }

    //    @Test
    //    public void testFragmentDisplaysQuestionCorrectly() {
    //        model.loadQuiz();
    //        model.getFocusedQuestion().postValue(0);
    //        onView(withId(R.id.question_title))
    //                .check(matches(withText("Question 1: The matches problem")));
    //        onView(withId(R.id.question_display))
    //                .check(matches(withText("How many matches can fit in a shoe of size 43?")));
    //        onView(withId(R.id.answer_fragment)).check(matches(isDisplayed()));
    //    }
}
