package ch.epfl.qedit.edit;

import static android.app.Activity.RESULT_OK;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.qedit.util.DragAndDropAction.dragAndDrop;
import static ch.epfl.qedit.util.Util.createTestQuiz;
import static ch.epfl.qedit.util.Util.createTestStringPool;
import static ch.epfl.qedit.util.Util.onDialog;
import static ch.epfl.qedit.view.edit.EditOverviewFragment.NEW_QUESTION_REQUEST_CODE;
import static ch.epfl.qedit.view.edit.EditOverviewFragment.QUESTION;
import static ch.epfl.qedit.view.home.HomeQuizListFragment.STRING_POOL;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.AllOf.allOf;

import android.app.Instrumentation;
import android.content.Intent;
import androidx.lifecycle.ViewModelProvider;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import ch.epfl.qedit.R;
import ch.epfl.qedit.model.Question;
import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.model.StringPool;
import ch.epfl.qedit.model.answer.MatrixFormat;
import ch.epfl.qedit.view.edit.EditOverviewFragment;
import ch.epfl.qedit.view.edit.EditQuestionActivity;
import ch.epfl.qedit.viewmodel.EditionViewModel;
import com.android21buttons.fragmenttestrule.FragmentTestRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class EditOverviewFragmentTest extends EditTest {
    private static final Quiz testQuiz = createTestQuiz();
    private static final StringPool stringPool = createTestStringPool("TestTitle");

    @Rule
    public final FragmentTestRule<?, EditOverviewFragment> testRule =
            FragmentTestRule.create(EditOverviewFragment.class, false, false);

    @Rule
    public final IntentsTestRule<EditQuestionActivity> resultTestRule =
            new IntentsTestRule<>(EditQuestionActivity.class, false, false);

    @Before
    public void setUp() {
        Intents.init();
        Quiz.Builder quizBuilder = new Quiz.Builder(testQuiz);

        EditionViewModel model =
                new ViewModelProvider(testRule.getActivity()).get(EditionViewModel.class);

        model.setQuizBuilder(quizBuilder);
        model.setStringPool(stringPool);

        testRule.launchFragment(new EditOverviewFragment());
    }

    @After
    public void commit() {
        testRule.finishActivity();
        Intents.release();
    }

    private void checkText(int position, String text) {
        itemView(position, android.R.id.text1).check(matches(withText(text)));
    }

    private void assertOverlayAt(int position, int size) {
        for (int i = 0; i < size; i++) {
            if (i == position) overlay(i).check(matches(isDisplayed()));
            else overlay(i).check(matches(not(isDisplayed())));
        }
    }

    @Test
    public void testCanSelectItem() {
        assertOverlayAt(-1, 5);
        item(0).perform(click());
        assertOverlayAt(0, 5);
        item(3).perform(click());
        assertOverlayAt(3, 5);
        item(3).perform(click());
        assertOverlayAt(-1, 5);
    }

    @Test
    public void testCanDeleteItem() {
        String firstQuestionTitle = stringPool.get(testQuiz.getQuestions().get(0).getTitle());
        onView(withText(firstQuestionTitle)).perform(click());
        itemView(0, R.id.delete_button).perform(click());

        onView(withText(testRule.getActivity().getString(R.string.warning_delete_question)))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

        onDialog(android.R.id.button2).perform(click());
        onView(withText(firstQuestionTitle)).check(matches(isDisplayed()));

        itemView(0, R.id.delete_button).perform(click());
        onDialog(android.R.id.button1).perform(click());
        onView(withText(firstQuestionTitle)).check(doesNotExist());

        assertOverlayAt(-1, 4);
    }

    @Test
    public void testCanAddButton() {
        onView(withId(R.id.add_question_button)).perform(click());

        intended(
                allOf(
                        hasComponent(EditQuestionActivity.class.getName()),
                        hasExtra(equalTo(STRING_POOL), instanceOf(StringPool.class))));
    }

    @Test
    public void testCanDragAndDrop() {
        String[] titles = getTitles();

        checkText(0, titles[0]);
        checkText(1, titles[1]);
        onView(withId(R.id.question_list)).perform(dragAndDrop(0, 1));
        checkText(0, titles[1]);
        checkText(1, titles[0]);

        item(0).perform(click());
        assertOverlayAt(0, 5);
        onView(withId(R.id.question_list)).perform(dragAndDrop(1, 0));
        assertOverlayAt(1, 5);

        item(4).perform(click());
        assertOverlayAt(4, 5);
        onView(withId(R.id.question_list)).perform(dragAndDrop(4, 0));
        assertOverlayAt(0, 5);

        checkText(0, titles[4]);
        checkText(1, titles[0]);
        checkText(2, titles[1]);
        checkText(3, titles[2]);
        checkText(4, titles[3]);
    }

    private String[] getTitles() {
        String[] titles = new String[testQuiz.getQuestions().size()];

        for (int i = 0; i < titles.length; ++i) {
            titles[i] = stringPool.get(testQuiz.getQuestions().get(i).getTitle());
        }

        return titles;
    }

    // TODO @Test
    public void testOnActivityResult() {
        StringPool stringPool = new StringPool();
        Question question =
                new Question(
                        stringPool.add("This is a new title"),
                        stringPool.add("This is a new text"),
                        MatrixFormat.singleField(MatrixFormat.Field.textField("", 20)));

        Intent dataIntent = new Intent();
        dataIntent.putExtra(QUESTION, question);
        dataIntent.putExtra(STRING_POOL, stringPool);

        intending(hasComponent(EditQuestionActivity.class.getName()))
                .respondWith(new Instrumentation.ActivityResult(RESULT_OK, dataIntent));
        resultTestRule
                .getActivity()
                .startActivityForResult(
                        new Intent(testRule.getFragment().getContext(), EditQuestionActivity.class),
                        NEW_QUESTION_REQUEST_CODE);
    }

    @Test
    public void testLaunchesEditQuestionActivity() {
        item(0).perform(click());
        itemView(0, R.id.edit_button).perform(click());

        intended(
                allOf(
                        hasComponent(EditQuestionActivity.class.getName()),
                        hasExtra(equalTo(STRING_POOL), instanceOf(StringPool.class))));
    }

    @Test
    public void testEmptyHint() {
        onView(withId(R.id.empty_list_hint)).check(matches(not(isDisplayed())));

        emptyQuizList(testQuiz);

        onView(withId(R.id.empty_list_hint)).check(matches(isDisplayed()));
    }
}
