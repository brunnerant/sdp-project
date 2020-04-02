package ch.epfl.qedit.edit;

import com.android21buttons.fragmenttestrule.FragmentTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import ch.epfl.qedit.R;
import ch.epfl.qedit.quiz.QuizFragmentsTestUsingDB;
import ch.epfl.qedit.view.edit.EditQuestionFragment;
import ch.epfl.qedit.view.quiz.QuestionFragment;
import ch.epfl.qedit.viewmodel.QuizViewModel;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

public class EditQuestionFragmentTest extends QuizFragmentsTestUsingDB {
    private QuizViewModel model;

    @Rule
    public final FragmentTestRule<?, EditQuestionFragment> editTestRule =
            FragmentTestRule.create(EditQuestionFragment.class, false, false);

    @Before
    public void setup() {
        model = super.setup(editTestRule, new EditQuestionFragment());
    }

    @After
    public void cleanup() {
        super.cleanup();
    }

    @Test
    public void testFragmentIsEmptyByDefault() {
        onView(withId(R.id.edit_question_title)).check(matches(withText("")));
        onView(withId(R.id.edit_question_display)).check(matches(withText("")));
    }

    @Test
    public void testFragmentDisplaysQuestionCorrectly() {
        model.getFocusedQuestion().postValue(0);
        onView(withId(R.id.edit_question_title))
                .check(matches(withText("Question 1 - The matches problem")));
        onView(withId(R.id.edit_question_display))
                .check(matches(withText("How many matches can fit in a shoe of size 43 ?")));
    }
}
