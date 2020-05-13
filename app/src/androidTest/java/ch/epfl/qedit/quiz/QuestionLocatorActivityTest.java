package ch.epfl.qedit.quiz;

import android.content.Intent;
import android.location.Location;

import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import ch.epfl.qedit.R;
import ch.epfl.qedit.backend.location.LocServiceFactory;
import ch.epfl.qedit.backend.location.MockLocService;
import ch.epfl.qedit.util.LocationHelper;
import ch.epfl.qedit.view.quiz.QuestionLocatorActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.fail;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4ClassRunner.class)
public class QuestionLocatorActivityTest extends LocationHelper {
    @Rule
    public final IntentsTestRule<QuestionLocatorActivity> testRule =
            new IntentsTestRule<>(QuestionLocatorActivity.class, false, false);
    UiDevice device;

    @Before
    public void init() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        Location questionLoc = new Location("");
        questionLoc.setLongitude(0);
        questionLoc.setLatitude(0);
        float questionRadius = 100;

        Intent intent = new Intent();
        intent.putExtra(QuestionLocatorActivity.QUESTION_LOCATION, questionLoc);
        intent.putExtra(QuestionLocatorActivity.QUESTION_RADIUS, questionRadius);

        testRule.launchActivity(intent);

        initLocationProvider(testRule.getActivity());
        LocServiceFactory.setInstance(new MockLocService(testRule.getActivity()));
    }

    @After
    public void cleanup() {
        cleanupLocationProvider(testRule.getActivity());
    }

    private void assertPermissionPopupVisible() {
        UiObject allow = device.findObject(new UiSelector().textMatches("(?i)allow.*"));
        UiObject deny = device.findObject(new UiSelector().text("(?i)deny.*"));

        if (!allow.exists() || !deny.exists())
            fail("Expected permission popup");
    }

    private void grantPermission(boolean allow) {
        UiObject button = device.findObject(new UiSelector().textMatches(allow ? "(?i)allow.*" : "(?i)deny.*"));
        try {
            button.click();
        } catch (UiObjectNotFoundException e) {
            fail("Expected permission popup");
        }
    }

    // Note that the order in which tests are executed is important, so that's why we
    // give them weird names. We have to be careful to test
    // the permission behaviour at the start, because once they were allowed, it is no longer
    // possible to test it. In a future PR, we might be able to mock the permission system
    // used by android.

    private String getString(int id) {
        return testRule.getActivity().getString(id);
    }

    @Test
    public void a_testPermissionDenied() {
        // The permission popup should appear
        assertPermissionPopupVisible();

        // Test that refusing the permission shows the right UI
        grantPermission(false);
        onView(withId(R.id.question_locator_text1)).check(matches(withText(getString(R.string.question_locator_found))));
        onView(withId(R.id.question_locator_text2)).check(matches(withText("")));
        onView(withId(R.id.question_locator_button)).check(matches(withText(getString(R.string.enable_location))));

        // Clicking on the button should show the popup again
        onView(withId(R.id.question_locator_button)).perform(click());
        assertPermissionPopupVisible();
    }

    @Test
    public void b_testPermissionAllowed() {

    }
}
