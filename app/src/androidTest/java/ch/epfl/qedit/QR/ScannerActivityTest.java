package ch.epfl.qedit.QR;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.qedit.view.home.HomeActivity.USER;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import android.content.Intent;
import android.os.Bundle;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;
import ch.epfl.qedit.model.User;
import ch.epfl.qedit.view.QR.ScannerActivity;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4ClassRunner.class)
public class ScannerActivityTest {
    private static final int TEST_RESULT_HANDLER = 1;

    @Rule
    public final IntentsTestRule<ScannerActivity> testRule =
            new IntentsTestRule<>(ScannerActivity.class, false, false);

    UiDevice device;

    @Before
    public void init() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    }

    public void launchActivity() {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        User user = new User("Jamy", "Gourmaud");
        bundle.putSerializable(USER, user);
        intent.putExtras(bundle);
        testRule.launchActivity(intent);
    }

    public static void assertPermissionRequestIsVisible(UiDevice device, String text) {
        UiObject allowButton = device.findObject(new UiSelector().text(text));
        if (!allowButton.exists()) {
            throw new AssertionError("No view with text " + text);
        }
    }

    public static void denyPermission(UiDevice device) throws UiObjectNotFoundException {
        UiObject denyButton = device.findObject(new UiSelector().text("DENY"));
        denyButton.click();
    }

    @Test
    public void a_permissionsAreRequested() throws UiObjectNotFoundException {
        launchActivity();
        assertPermissionRequestIsVisible(device, "ALLOW");
        assertPermissionRequestIsVisible(device, "DENY");

        denyPermission(device);
        testRule.finishActivity();
    }

    @Test
    public void b_acceptPermission() throws UiObjectNotFoundException {
        launchActivity();
        UiObject allowButton = device.findObject(new UiSelector().text("ALLOW"));
        allowButton.click();

        onView(withText("Permission Granted"))
                .inRoot(withDecorView(not(is(testRule.getActivity().getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
    }
}
