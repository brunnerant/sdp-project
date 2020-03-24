package ch.epfl.qedit.question;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.os.Bundle;
import androidx.lifecycle.ViewModelProvider;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import ch.epfl.qedit.R;
import ch.epfl.qedit.backend.database.DatabaseFactory;
import ch.epfl.qedit.backend.database.MockDBService;
import ch.epfl.qedit.model.Question;
import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.view.quiz.QuestionFragment;
import ch.epfl.qedit.viewmodel.QuizViewModel;
import com.android21buttons.fragmenttestrule.FragmentTestRule;
import java.util.Arrays;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4ClassRunner.class)
public class QuestionFragmentTest {

    private IdlingResource idlingResource;
    private QuizViewModel model;

    @Rule
    public final FragmentTestRule<?, QuestionFragment> testRule =
            FragmentTestRule.create(QuestionFragment.class, false, false);

    @Before
    public void init() {
        MockDBService dbService = new MockDBService();
        idlingResource = dbService.getIdlingResource();
        IdlingRegistry.getInstance().register(idlingResource);
        DatabaseFactory.setInstance(dbService);
        model = new ViewModelProvider(testRule.getActivity()).get(QuizViewModel.class);

        Quiz quiz =
                new Quiz(
                        "QuizOverviewFragmentTest",
                        Arrays.asList(
                                new Question(
                                        "The matches problem",
                                        "How many matches can fit in a shoe of size 43 ?",
                                        "matrix3x3"),
                                new Question(
                                        "Pigeons",
                                        "How many pigeons are there on Earth ? (Hint: do not count yourself)",
                                        "matrix1x1"),
                                new Question("KitchenBu", "Oyster", "matrix1x1"),
                                new Question(
                                        "Everything",
                                        "What is the answer to life the univere and everything ?",
                                        "matrix3x3"),
                                new Question(
                                        "Banane", "Combien y a-t-il de bananes ?", "matrix1x1")));

        Bundle bundle = new Bundle();
        bundle.putSerializable("quiz", quiz);

        QuestionFragment questionFragment = new QuestionFragment();
        questionFragment.setArguments(bundle);

        testRule.launchFragment(questionFragment);
    }

    @After
    public void cleanup() {
        IdlingRegistry.getInstance().unregister(idlingResource);
    }

    @Test
    public void testFragmentIsEmptyByDefault() {
        onView(withId(R.id.question_title)).check(matches(withText("")));
        onView(withId(R.id.question_display)).check(matches(withText("")));
        onView(withId(R.id.answer_fragment)).check(doesNotExist());
    }

    @Test
    public void testFragmentDisplaysQuestionCorrectly() {
        model.getFocusedQuestion().postValue(0);
        onView(withId(R.id.question_title))
                .check(matches(withText("Question 1 - The matches problem")));
        onView(withId(R.id.question_display))
                .check(matches(withText("How many matches can fit in a shoe of size 43 ?")));
        onView(withId(R.id.answersTable)).check(matches(isDisplayed()));
    }
}
