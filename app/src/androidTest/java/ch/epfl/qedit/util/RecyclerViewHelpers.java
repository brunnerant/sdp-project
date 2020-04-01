package ch.epfl.qedit.util;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static ch.epfl.qedit.util.RecyclerViewMatcher.withRecyclerView;

import androidx.test.espresso.ViewInteraction;
import ch.epfl.qedit.R;

public class RecyclerViewHelpers {
    private int recyclerViewId;

    public RecyclerViewHelpers(int recyclerViewId) {
        this.recyclerViewId = recyclerViewId;
    }

    public void scrollTo(int position) {
        onView(withId(recyclerViewId)).perform(scrollToPosition(position));
    }

    public ViewInteraction itemView(int position, int id) {
        scrollTo(position);
        return onView(withRecyclerView(recyclerViewId).atPositionOnView(position, id));
    }

    public ViewInteraction overlay(int position) {
        return itemView(position, R.id.overlay_buttons);
    }

    public ViewInteraction item(int position) {
        scrollTo(position);
        return onView(withRecyclerView(recyclerViewId).atPosition(position));
    }
}
