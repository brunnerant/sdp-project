package ch.epfl.qedit.sensor;

import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import ch.epfl.qedit.view.sensor.ScannerActivity;
import org.junit.Rule;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4ClassRunner.class)
public class ScannerActivityTest {
    @Rule
    public final IntentsTestRule<ScannerActivity> testRule =
            new IntentsTestRule<>(ScannerActivity.class, false, false);

    public void launchActivity() {
        testRule.launchActivity(null);
    }

    public void cleanup() {
        testRule.finishActivity();
        ;
    }

    /* private UiDevice device;

    public void assertViewWithTestIsVisible(UiDevice device, String text) {
        UiObject allowButton = device.findObject(new UiSelector().text(text));
        if (!allowButton.exists()) {
            throw new AssertionError("View with text <" + text + "> not found!");
        }
    }

    public static void denyCurrentPermission(UiDevice device) throws UiObjectNotFoundException {
        UiObject denyButton = device.findObject(new UiSelector().text("DENY"));
        denyButton.click();
    }*/

    /*  @Test
    public void checkPermissionsAreRequested() {
        launchActivity();
        onView(withText("ALLOW")).inRoot(isDialog()).check(matches(isDisplayed()));
        onView(withText("DENY")).inRoot(isDialog()).check(matches(isDisplayed()));
        onView(withText("DENY")).inRoot(isDialog()).perform(click());
        cleanup();
    }

    @Test
    public void checkPermissionGranted() throws UiObjectNotFoundException {
        launchActivity();
        onView(withText("ALLOW")).inRoot(isDialog()).perform(click());

        cleanup();
    }*/
    /* @Test
    public void checkResultHandled(){
        testRule.getActivity()
                .handleResult(new Result("Hello there", null, null, BarcodeFormat.QR_CODE));
        onView(withText("Hello there")).check(matches(isDisplayed()));
    }*/
}
