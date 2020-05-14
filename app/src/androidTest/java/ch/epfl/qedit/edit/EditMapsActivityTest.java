package ch.epfl.qedit.edit;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import ch.epfl.qedit.R;
import ch.epfl.qedit.view.edit.EditMapsActivity;
import com.google.android.gms.maps.SupportMapFragment;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class EditMapsActivityTest {
    @Rule
    public final IntentsTestRule<EditMapsActivity> testRule =
            new IntentsTestRule<>(EditMapsActivity.class, false, false);

    @Before
    public void setUp() {
        Intent intent = new Intent();
        testRule.launchActivity(intent);
    }

    @Test
    public void testThatFActivityIsDisplayed() {
        onView(withText("Map")).check(matches(isDisplayed()));
        onView(withId(R.id.map)).check(matches(isDisplayed()));
        onView(withId(R.id.map)).perform(click());
    }

    @Test
    public void testMap() {
        new Handler(Looper.getMainLooper())
                .post(
                        () -> {
                            SupportMapFragment mapFragment =
                                    (SupportMapFragment)
                                            testRule.getActivity()
                                                    .getSupportFragmentManager()
                                                    .findFragmentById(R.id.map);
                            mapFragment.getMapAsync(testRule.getActivity());
                            onView(withId(R.id.map)).perform(click());
                        });
    }

    @Test(expected = NullPointerException.class)
    public void testErrorOnMapNull() {
        testRule.getActivity().onMapReady(null);
    }
}
