package ch.epfl.qedit.edit;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.qedit.util.RecyclerViewMatcher.withRecyclerView;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;

import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.matcher.ViewMatchers;

import static androidx.test.espresso.contrib.RecyclerViewActions.*;

import ch.epfl.qedit.R;
import ch.epfl.qedit.view.edit.EditOverviewFragment;
import com.android21buttons.fragmenttestrule.FragmentTestRule;
import org.junit.Rule;
import org.junit.Test;

import java.util.Arrays;

public class EditOverviewFragmentTest {
    @Rule
    public final FragmentTestRule<?, EditOverviewFragment> testRule =
            FragmentTestRule.create(EditOverviewFragment.class);

    public static void scrollTo(int position) {
        onView(withId(R.id.question_list)).perform(scrollToPosition(position));
    }

    public static ViewInteraction itemView(int position, int id, ViewMatchers... matchers) {
        scrollTo(position);
        return onView(
                withRecyclerView(R.id.question_list)
                        .atPositionOnView(position, id));
    }

    public static ViewInteraction overlay(int position) {
        return itemView(position, R.id.overlay_buttons);
    }

    public static ViewInteraction item(int position) {
        scrollTo(position);
        return onView(withRecyclerView(R.id.question_list).atPosition(position));
    }

    public void testOverlayAt(int position, int size) {
        for (int i = 0; i < size; i++) {
            if (i == position)
                overlay(i).check(matches(isDisplayed()));
            else
                overlay(i).check(matches(not(isDisplayed())));
        }
    }

    @Test
    public void testCanSelectItem() {
        testOverlayAt(-1, 5);
        item(0).perform(click());
        testOverlayAt(0, 5);
        item(3).perform(click());
        testOverlayAt(3, 5);
        item(3).perform(click());
        testOverlayAt(-1, 5);
    }

    @Test
    public void testCanDeleteItem() {
        item(0).perform(click());
        itemView(0, R.id.delete_button).perform(click());
        onView(withText("Q1")).check(doesNotExist());
        testOverlayAt(-1, 4);
    }

    @Test
    public void testCanAddItem() {
        onView(withText("Q6")).check(doesNotExist());
        onView(withId(R.id.add_question_button)).perform(click());
        onView(withText("Q6")).check(matches(isDisplayed()));
    }
}
