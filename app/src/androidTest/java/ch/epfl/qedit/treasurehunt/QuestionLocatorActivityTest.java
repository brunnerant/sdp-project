package ch.epfl.qedit.treasurehunt;

import android.Manifest;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.function.Consumer;

import ch.epfl.qedit.R;
import ch.epfl.qedit.backend.location.LocServiceFactory;
import ch.epfl.qedit.backend.location.MockLocService;
import ch.epfl.qedit.backend.permission.MockPermManager;
import ch.epfl.qedit.backend.permission.PermManagerFactory;
import ch.epfl.qedit.view.treasurehunt.QuestionLocatorActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(AndroidJUnit4ClassRunner.class)
public class QuestionLocatorActivityTest {
    @Rule
    public final IntentsTestRule<QuestionLocatorActivity> testRule =
            new IntentsTestRule<>(QuestionLocatorActivity.class, false, false);

    MockLocService locService;
    MockPermManager permManager;
    IdlingResource idlingResource;

    private static final String[] permissions =
            new String[] {
                Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
            };

    public void init(Consumer<MockPermManager> action) {
        Location questionLoc = new Location("");
        questionLoc.setLongitude(0);
        questionLoc.setLatitude(0);
        float questionRadius = 100;

        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        intent.putExtra(QuestionLocatorActivity.QUESTION_LOCATION, questionLoc);
        bundle.putDouble(QuestionLocatorActivity.QUESTION_RADIUS, questionRadius);
        intent.putExtras(bundle);

        // We mock the location and the permission services
        LocServiceFactory.setInstance(
                context -> {
                    locService = new MockLocService(context);
                    return locService;
                });
        permManager = spy(MockPermManager.class);
        PermManagerFactory.setInstance(permManager);
        idlingResource = permManager.getIdlingResource();
        IdlingRegistry.getInstance().register(idlingResource);

        // We let the test setup the permission manager
        action.accept(permManager);

        // Finally, we launch the activity
        testRule.launchActivity(intent);
    }

    @After
    public void cleanup() {
        IdlingRegistry.getInstance().unregister(idlingResource);
        LocServiceFactory.reset();
    }

    private String getString(int id) {
        return testRule.getActivity().getString(id);
    }

    // Since we have to check the view in a lot of different situations, this helper method shortens
    // the tests a lot.
    private void checkView(Matcher<String> text1, Matcher<String> text2, int buttonTextId) {
        onView(withId(R.id.question_locator_text1)).check(matches(withText(text1)));
        onView(withId(R.id.question_locator_text2)).check(matches(withText(text2)));

        if (buttonTextId == -1)
            onView(withId(R.id.question_locator_button)).check(matches(not(isDisplayed())));
        else
            onView(withId(R.id.question_locator_button))
                    .check(matches(withText(getString(buttonTextId))));
    }

    private void checkView(String text1, String text2, int buttonTextId) {
        checkView(equalTo(text1), equalTo(text2), buttonTextId);
    }

    private void checkRequest() {
        verify(permManager)
                .requestPermissions(
                        eq(testRule.getActivity()),
                        any(),
                        eq(Manifest.permission.ACCESS_FINE_LOCATION),
                        eq(Manifest.permission.ACCESS_COARSE_LOCATION));
    }

    private void setLocation(double longitude, double latitude) {
        testRule.getActivity().runOnUiThread(() -> locService.setLocation(longitude, latitude));
    }

    private void testNormalFlow() {
        // The location was not received, so it should display an error message
        checkView(getString(R.string.question_locator_error), "", R.string.question_locator_move);

        // Now that the location was received, it should display something
        setLocation(0, 10);
        checkView(
                startsWith(getString(R.string.question_locator_distance)),
                startsWith(getString(R.string.question_locator_bearing)),
                R.string.question_locator_move);

        // Now that we arrived at the question, the UI should change
        setLocation(0, 0);
        checkView(getString(R.string.question_locator_found), "", R.string.question_locator_answer);
    }

    @Test
    public void testApproachQuestion() {
        init(
                permManager -> {
                    // We test the normal scenario when the permissions are granted
                    permManager.grantPermissions(permissions);
                });

        // The permissions were already granted, so they shouldn't be asked
        verify(permManager, never()).requestPermissions(any(), any(), any());
        testNormalFlow();

        // Clicking on the button should finish the activity
        assertFalse(testRule.getActivity().isFinishing());
        onView(withId(R.id.question_locator_button)).perform(click());
        assertTrue(testRule.getActivity().isFinishing());
    }

    @Test
    public void testPermissionRequested() {
        init(permManager -> {});

        // The view should display an unknown location error
        checkView(getString(R.string.question_locator_error), "", -1);

        // The activity should request permissions
        checkRequest();
    }

    @Test
    public void testPermissionRefused() {
        init(
                permManager -> {
                    permManager.refusePermissions(permissions);
                });

        // The activity should request permissions
        checkRequest();
        reset(permManager);

        // The view should display an unknown location error and a request button
        checkView(getString(R.string.question_locator_error), "", R.string.enable_location);

        // Clicking on the button with granted permissions should restart the normal flow
        permManager.resetPermissions(permissions);
        onView(withId(R.id.question_locator_button)).perform(click());
        checkRequest();
        testNormalFlow();
    }

    @Test
    public void testPermissionDenied() {
        init(
                permManager -> {
                    permManager.denyPermissions(permissions);
                });

        // The activity should request permissions
        checkRequest();

        // Now, the request permission button should not be displayed
        checkView(getString(R.string.question_locator_error), "", -1);
    }
}
