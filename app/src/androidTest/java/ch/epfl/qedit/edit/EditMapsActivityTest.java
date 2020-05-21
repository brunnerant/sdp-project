package ch.epfl.qedit.edit;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Intent;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import ch.epfl.qedit.R;
import ch.epfl.qedit.view.edit.EditMapsActivity;
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
    public void testThatActivityIsDisplayed() {
        onView(withId(R.id.map)).check(matches(isDisplayed()));
    }
}
