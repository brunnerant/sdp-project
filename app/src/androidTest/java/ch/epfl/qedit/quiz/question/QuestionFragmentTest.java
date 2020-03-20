package ch.epfl.qedit.quiz.question;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.lifecycle.ViewModelProvider;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import ch.epfl.qedit.R;
import ch.epfl.qedit.backend.database.DatabaseFactory;
import ch.epfl.qedit.backend.database.MockDBService;
import ch.epfl.qedit.view.quiz.QuestionFragment;
import ch.epfl.qedit.viewmodel.QuizViewModel;
import com.android21buttons.fragmenttestrule.FragmentTestRule;
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
            FragmentTestRule.create(QuestionFragment.class);

    @Before
    public void init() {
        MockDBService dbService = new MockDBService();
        idlingResource = dbService.getIdlingResource();
        IdlingRegistry.getInstance().register(idlingResource);
        DatabaseFactory.setInstance(dbService);
        model = new ViewModelProvider(testRule.getActivity()).get(QuizViewModel.class);
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
        model.loadQuiz("quiz0");
        model.getFocusedQuestion().postValue(0);
        onView(withId(R.id.question_title)).check(matches(withText("Question 1 - Banane")));
        onView(withId(R.id.question_display))
                .check(matches(withText("Combien y a-t-il de bananes ?")));
        onView(withId(R.id.answersTable)).check(matches(isDisplayed()));
    }
}
