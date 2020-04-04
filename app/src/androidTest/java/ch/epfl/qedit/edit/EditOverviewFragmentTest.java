package ch.epfl.qedit.edit;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.qedit.util.DragAndDropAction.dragAndDrop;
import static org.hamcrest.Matchers.not;

import androidx.test.rule.ActivityTestRule;
import ch.epfl.qedit.R;
import ch.epfl.qedit.util.RecyclerViewHelpers;
import ch.epfl.qedit.view.edit.EditOverviewFragment;
import ch.epfl.qedit.view.edit.EditQuizActivity;
import com.android21buttons.fragmenttestrule.FragmentTestRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class EditOverviewFragmentTest extends RecyclerViewHelpers {
    @Rule
    public final FragmentTestRule<?, EditOverviewFragment> testRule =
            FragmentTestRule.create(EditOverviewFragment.class);

    @Rule
    public ActivityTestRule<EditQuizActivity> fRule =
            new ActivityTestRule<>(EditQuizActivity.class);

    @Before
    public void setUp() throws Exception {
        fRule.getActivity().getSupportFragmentManager().beginTransaction();
    }

    @After
    public void commit() {
        fRule.getActivity().finish();
        testRule.finishActivity();
    }

    public EditOverviewFragmentTest() {
        super(R.id.question_list);
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
        item(0).perform(click());
        itemView(0, R.id.delete_button).perform(click());
        onView(withText("Q1")).check(doesNotExist());
        assertOverlayAt(-1, 4);
    }

    @Test
    public void testCanAddItem() {
        onView(withText("Q6")).check(doesNotExist());
        onView(withId(R.id.add_question_button)).perform(click());
        onView(withText("Q6")).check(matches(isDisplayed()));
    }

    @Test
    public void testCanDragAndDrop() {
        checkText(0, "Q1");
        checkText(1, "Q2");
        onView(withId(R.id.question_list)).perform(dragAndDrop(0, 1));
        checkText(0, "Q2");
        checkText(1, "Q1");

        item(0).perform(click());
        assertOverlayAt(0, 5);
        onView(withId(R.id.question_list)).perform(dragAndDrop(1, 0));
        assertOverlayAt(1, 5);

        item(4).perform(click());
        assertOverlayAt(4, 5);
        onView(withId(R.id.question_list)).perform(dragAndDrop(4, 0));
        assertOverlayAt(0, 5);

        checkText(0, "Q5");
        checkText(1, "Q1");
        checkText(2, "Q2");
        checkText(3, "Q3");
        checkText(4, "Q4");
    }
}
