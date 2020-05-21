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
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.qedit.util.DragAndDropAction.dragAndDrop;
import static ch.epfl.qedit.util.Util.createTestQuiz;
import static ch.epfl.qedit.util.Util.createTestStringPool;
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
import ch.epfl.qedit.util.RecyclerViewHelpers;
import ch.epfl.qedit.view.edit.EditOverviewFragment;
import ch.epfl.qedit.view.edit.EditQuestionActivity;
import ch.epfl.qedit.viewmodel.EditionViewModel;
import com.android21buttons.fragmenttestrule.FragmentTestRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class EditOverviewFragmentTest extends RecyclerViewHelpers {
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

    public EditOverviewFragmentTest() {
        super(R.id.question_list);
    }

    private void checkText(int position, String text) {
        itemView(position, android.R.id.text1).check(matches(withText(text)));
    }

    @Test
    public void testCanDeleteItem() {
        itemView(0, R.id.list_item_three_dots).perform(click());
        clickOnPopup(testRule.getActivity(), R.string.menu_delete);
        onView(withText(testQuiz.getQuestions().get(0).getTitle())).check(doesNotExist());
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

        onView(withId(R.id.question_list)).perform(dragAndDrop(1, 0));
        onView(withId(R.id.question_list)).perform(dragAndDrop(4, 0));

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
                        MatrixFormat.singleField(MatrixFormat.Field.textField("")));

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

//        intending(hasComponent(EditMapsActivity.class.getName()))
//                .respondWith(new Instrumentation.ActivityResult(MAP_REQUEST_CODE, dataIntent));
//        testRule.getActivity()
//                .startActivityForResult(
//                        new Intent(testRule.getActivity(), EditMapsActivity.class),
//                        MAP_REQUEST_CODE);
    }

    @Test
    public void testLaunchesEditQuestionActivity() {
        item(0).perform(click());

        itemView(0, R.id.list_item_three_dots).perform(click());
        clickOnPopup(testRule.getActivity(), R.string.menu_edit);

        intended(
                allOf(
                        hasComponent(EditQuestionActivity.class.getName()),
                        hasExtra(equalTo(STRING_POOL), instanceOf(StringPool.class))));
    }

    @Test
    public void testEmptyHint() {
        onView(withId(R.id.empty_list_hint)).check(matches(not(isDisplayed())));

        for (int i = 0; i < testQuiz.getQuestions().size(); ++i) {
            itemView(0, R.id.list_item_three_dots).perform(click());
            clickOnPopup(testRule.getActivity(), R.string.menu_delete);
        }

        onView(withId(R.id.empty_list_hint)).check(matches(isDisplayed()));
    }
}
