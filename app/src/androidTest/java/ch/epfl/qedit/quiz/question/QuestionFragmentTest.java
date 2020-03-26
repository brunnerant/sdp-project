package ch.epfl.qedit.quiz.question;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import ch.epfl.qedit.R;
import ch.epfl.qedit.quiz.QuizFragmentsTestUsingDB;
import ch.epfl.qedit.view.quiz.QuestionFragment;
import ch.epfl.qedit.viewmodel.QuizViewModel;
import com.android21buttons.fragmenttestrule.FragmentTestRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4ClassRunner.class)
public class QuestionFragmentTest extends QuizFragmentsTestUsingDB {
    private QuizViewModel model;

    @Rule
    public final FragmentTestRule<?, QuestionFragment> testRule =
            FragmentTestRule.create(QuestionFragment.class, false, false);

    @Before
    public void init() {
        model = super.setup(testRule, new QuestionFragment());
    }

    @After
    public void cleanup() {
        super.cleanup();
    }

    @Test
    public void testFragmentIsEmptyByDefault() {
        onView(withId(R.id.question_title)).check(matches(withText("")));
        onView(withId(R.id.question_display)).check(matches(withText("")));
        onView(withId(R.id.answer_fragment)).check(doesNotExist());
    }

    @SuppressWarnings("SpellCheckingInspection")
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
