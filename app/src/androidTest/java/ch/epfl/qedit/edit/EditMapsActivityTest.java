package ch.epfl.qedit.edit;

import android.content.Intent;
import android.os.Bundle;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.intent.rule.IntentsTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import ch.epfl.qedit.R;
import ch.epfl.qedit.model.StringPool;
import ch.epfl.qedit.view.edit.EditMapsActivity;
import ch.epfl.qedit.view.edit.EditSettingsActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.qedit.model.StringPool.TITLE_ID;
import static ch.epfl.qedit.view.home.HomeQuizListFragment.STRING_POOL;

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
    public void testThatFragmentsAreDisplayed() {
        onView(withText("Map")).check(matches(isDisplayed()));
    }
}
