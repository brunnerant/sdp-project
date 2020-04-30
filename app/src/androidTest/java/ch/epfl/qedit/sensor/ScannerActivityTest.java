package ch.epfl.qedit.sensor;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;
import ch.epfl.qedit.view.sensor.ScannerActivity;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4ClassRunner.class)
public class ScannerActivityTest {
    @Rule public ActivityTestRule testRule = new ActivityTestRule(ScannerActivity.class);

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
        assertPermissionRequestIsVisible(device, "ALLOW");
        assertPermissionRequestIsVisible(device, "DENY");

        denyPermission(device);
    }

    @Test
    public void b_acceptPermission() throws UiObjectNotFoundException {
        UiObject allowButton = device.findObject(new UiSelector().text("ALLOW"));
        allowButton.click();
    }
}
