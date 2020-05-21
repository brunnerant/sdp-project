package ch.epfl.qedit.util;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.qedit.util.RecyclerViewMatcher.withRecyclerView;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import android.app.Activity;
import androidx.test.espresso.ViewInteraction;

public class RecyclerViewHelpers {
    private int recyclerViewId;

    protected RecyclerViewHelpers(int recyclerViewId) {
        this.recyclerViewId = recyclerViewId;
    }

    protected void scrollTo(int position) {
        onView(withId(recyclerViewId)).perform(scrollToPosition(position));
    }

    protected ViewInteraction itemView(int position, int id) {
        scrollTo(position);
        return onView(withRecyclerView(recyclerViewId).atPositionOnView(position, id));
    }

    protected ViewInteraction item(int position) {
        scrollTo(position);
        return onView(withRecyclerView(recyclerViewId).atPosition(position));
    }

    protected void clickOnPopup(Activity activity, int stringId) {
        onView(withText(stringId))
                .inRoot(withDecorView(not(is(activity.getWindow().getDecorView()))))
                .perform(click());
    }
}
