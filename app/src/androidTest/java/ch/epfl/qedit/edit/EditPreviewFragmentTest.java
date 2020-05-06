package ch.epfl.qedit.edit;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.qedit.model.StringPool.TITLE_ID;
import static ch.epfl.qedit.util.Util.createMockQuiz;

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
    private static final String testTitle = "TestTitle";
    private static final String testNoTitle = "No TestTitle";
    private static final String testNoText = "No TestQuestionText";

    private static final Quiz mockQuiz = createMockQuiz(testTitle);
    private EditionViewModel model;

    @Rule
    public final FragmentTestRule<?, EditPreviewFragment> testRule =
            FragmentTestRule.create(EditPreviewFragment.class, false, false);

    @Before
    public void setUp() {
        StringPool stringPool = new StringPool();
        stringPool.update(TITLE_ID, testTitle);

        Quiz.Builder quizBuilder = new Quiz.Builder(mockQuiz);

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
    }

    @Test
    public void testFragmentDisplaysFilledQuestionCorrectly() {
        Question question = mockQuiz.getQuestions().get(0);
        model.getFocusedQuestion().postValue(0);
        onView(withId(R.id.question_title)).check(matches(withText(question.getTitle())));
        onView(withId(R.id.question_display)).check(matches(withText(question.getText())));
    }

    // @Test TODO
    public void testFragmentDisplaysEmptyQuestionCorrectly() {
        model.getFocusedQuestion().postValue(5);
        onView(withId(R.id.question_title)).check(matches(withText(testNoTitle)));
        onView(withId(R.id.question_display)).check(matches(withText(testNoText)));
    }
}
