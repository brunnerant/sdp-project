package ch.epfl.qedit.util;

import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static org.hamcrest.core.AllOf.allOf;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.GeneralLocation;
import androidx.test.espresso.action.MotionEvents;
import androidx.test.espresso.action.Press;
import org.hamcrest.Matcher;

/**
 * A custom view action to perform a long-press drag and drop. It was copied from
 * https://stackoverflow.com/questions/48044959/android-ui-test-long-click-and-drag.
 */
public class DragAndDropAction implements ViewAction {

    private final int from;
    private final int to;

    private DragAndDropAction(int from, int to) {
        this.from = from;
        this.to = to;
    }

    public static DragAndDropAction dragAndDrop(int from, int to) {
        return new DragAndDropAction(from, to);
    }

    @Override
    public Matcher<View> getConstraints() {
        return allOf(isDisplayed(), isAssignableFrom(RecyclerView.class));
    }

    @Override
    public String getDescription() {
        return "drag and drop on recycler view";
    }

    @Override
    public void perform(UiController uiController, View view) {
        final RecyclerView recyclerView = (RecyclerView) view;

        recyclerView.scrollToPosition(from);
        uiController.loopMainThreadUntilIdle();

        final View sourceView = recyclerView.findViewHolderForAdapterPosition(from).itemView;
        final float[] sourceCoord = GeneralLocation.VISIBLE_CENTER.calculateCoordinates(sourceView);
        final float[] sourcePrecision = Press.FINGER.describePrecision();

        final MotionEvent downEvent =
                MotionEvents.sendDown(uiController, sourceCoord, sourcePrecision).down;

        // Factor 1.5 is needed, otherwise a long press is not safely detected.
        final long longPressTimeout = (long) (ViewConfiguration.getLongPressTimeout() * 1.5f);
        uiController.loopMainThreadForAtLeast(longPressTimeout);

        // Drag to the position
        recyclerView.scrollToPosition(to);
        uiController.loopMainThreadUntilIdle();
        final View targetView = recyclerView.findViewHolderForAdapterPosition(to).itemView;
        final float[] targetCoord;

        if (to > from) targetCoord = GeneralLocation.BOTTOM_CENTER.calculateCoordinates(targetView);
        else targetCoord = GeneralLocation.TOP_CENTER.calculateCoordinates(targetView);

        float[][] steps = interpolate(sourceCoord, targetCoord);

        for (int i = 0; i < steps.length; i++) {
            if (!MotionEvents.sendMovement(uiController, downEvent, steps[i]))
                MotionEvents.sendCancel(uiController, downEvent);
        }

        // Release
        if (!MotionEvents.sendUp(uiController, downEvent, targetCoord))
            MotionEvents.sendCancel(uiController, downEvent);
    }

    private float[][] interpolate(float[] start, float[] end) {
        int SWIPE_EVENT_COUNT = 10;
        final float[][] result = new float[SWIPE_EVENT_COUNT][2];

        for (int j = 0; j < 2; j++) {
            for (int i = 0; i < SWIPE_EVENT_COUNT; i++)
                result[i][j] = start[j] + (end[j] - start[j]) * (i + 1) / SWIPE_EVENT_COUNT;
        }

        return result;
    }
}
