package ch.epfl.qedit.QR;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;
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
        testRule.launchActivity(null);
        assertPermissionRequestIsVisible(device, "ALLOW");
        assertPermissionRequestIsVisible(device, "DENY");

        denyPermission(device);
        testRule.finishActivity();
    }

    @Test
    public void b_acceptPermission() throws UiObjectNotFoundException {
        testRule.launchActivity(null);
        UiObject allowButton = device.findObject(new UiSelector().text("ALLOW"));
        allowButton.click();

        onView(withText("Permission Granted"))
                .inRoot(withDecorView(not(is(testRule.getActivity().getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
    }
}
