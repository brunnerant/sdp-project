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
    public void setup() {
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
        onView(withId(R.id.answer_table)).check(doesNotExist());
    }

    @Test
    public void testFragmentDisplaysQuestionCorrectly() {
        model.getFocusedQuestion().postValue(0);

        onView(withId(R.id.question_title))
                .check(matches(withText("Question 1 - The matches problem")));
        onView(withId(R.id.question_display))
                .check(matches(withText("How many matches can fit in a shoe of size 43?")));
        onView(withId(R.id.answer_table)).check(matches(isDisplayed()));
    }

    // @Test TODO
    public void testAnswerFormatDispatch() {
        model.getFocusedQuestion().postValue(0);

        onView(withId(R.id.question_title))
                .check(matches(withText("Question 1 - The matches problem")));
        onView(withId(R.id.question_display))
                .check(matches(withText("How many matches can fit in a shoe of size 43 ?")));
        onView(withId(R.id.answer_table)).check(matches(isDisplayed()));

        model.getFocusedQuestion().postValue(1);

        onView(withId(R.id.question_title)).check(matches(withText("Question 2 - Title 2")));
        onView(withId(R.id.question_display)).check(matches(withText("Empty answer format")));
        //        onView(withId(R.id.testAnswerFormatTextView)).check(matches(isDisplayed()));
        //        onView(withId(R.id.testAnswerFormatTextView))
        //                .check(
        //                        matches(
        //                                withText(
        //                                        testRule.getFragment()
        //                                                .getResources()
        //
        // .getString(R.string.empty_answer_format_message))));
    }
}
