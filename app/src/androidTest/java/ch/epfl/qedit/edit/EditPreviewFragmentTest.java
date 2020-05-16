package ch.epfl.qedit.edit;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.qedit.util.Util.createTestQuiz;
import static ch.epfl.qedit.util.Util.createTestStringPool;
import static org.hamcrest.core.IsNot.not;

import androidx.lifecycle.ViewModelProvider;
import ch.epfl.qedit.R;
import ch.epfl.qedit.model.Question;
import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.model.StringPool;
import ch.epfl.qedit.view.edit.EditPreviewFragment;
import ch.epfl.qedit.viewmodel.EditionViewModel;
import com.android21buttons.fragmenttestrule.FragmentTestRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class EditPreviewFragmentTest {
    private static final Quiz testQuiz = createTestQuiz();
    private static final StringPool stringPool = createTestStringPool("TestTitle");

    private EditionViewModel model;

    @Rule
    public final FragmentTestRule<?, EditPreviewFragment> testRule =
            FragmentTestRule.create(EditPreviewFragment.class, false, false);

    @Before
    public void setUp() {
        Quiz.Builder quizBuilder = new Quiz.Builder(testQuiz);

        model = new ViewModelProvider(testRule.getActivity()).get(EditionViewModel.class);

        model.setQuizBuilder(quizBuilder);
        model.setStringPool(stringPool);

        testRule.launchFragment(new EditPreviewFragment());
    }

    @After
    public void cleanUp() {
        testRule.finishActivity();
    }

    @Test
    public void testFragmentIsEmptyByDefault() {
        onView(withId(R.id.question_title)).check(matches(withText("")));
        onView(withId(R.id.question_display)).check(matches(withText("")));
        onView(withId(R.id.answer_fragment_container)).check(matches(not(isDisplayed())));
    }

    @Test
    public void testFragmentDisplaysFilledQuestionCorrectly() {
        Question question = testQuiz.getQuestions().get(0);
        model.getFocusedQuestion().postValue(0);

        onView(withId(R.id.question_title))
                .check(matches(withText(stringPool.get(question.getTitle()))));
        onView(withId(R.id.question_display))
                .check(matches(withText(stringPool.get(question.getText()))));
        onView(withId(R.id.answer_fragment_container)).check(matches(isDisplayed()));
    }
}
