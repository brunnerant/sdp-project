package ch.epfl.qedit.edit;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static ch.epfl.qedit.util.RecyclerViewMatcher.withRecyclerView;
import static org.hamcrest.Matchers.not;

import androidx.test.espresso.ViewInteraction;
import ch.epfl.qedit.R;
import ch.epfl.qedit.view.edit.EditOverviewFragment;
import com.android21buttons.fragmenttestrule.FragmentTestRule;
import org.junit.Rule;
import org.junit.Test;

public class EditOverviewFragmentTest {
    @Rule
    public final FragmentTestRule<?, EditOverviewFragment> testRule =
            FragmentTestRule.create(EditOverviewFragment.class);

    public static ViewInteraction overlay(int position) {
        return onView(
                withRecyclerView(R.id.question_list)
                        .atPositionOnView(position, R.id.overlay_buttons));
    }

    public static ViewInteraction item(int position) {
        return onView(withRecyclerView(R.id.question_list).atPosition(position));
    }

    @Test
    public void testCanSelectItem() {
        overlay(0).check(matches(not(isDisplayed())));
        item(0).perform(click());
        overlay(0).check(matches(isDisplayed()));
        item(0).perform(click());
        overlay(0).check(matches(not(isDisplayed())));
    }
}
