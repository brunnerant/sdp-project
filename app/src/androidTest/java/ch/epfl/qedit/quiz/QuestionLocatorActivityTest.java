package ch.epfl.qedit.quiz;

import android.content.Intent;
import android.location.Location;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;
import ch.epfl.qedit.backend.location.LocServiceFactory;
import ch.epfl.qedit.backend.location.MockLocService;
import ch.epfl.qedit.view.quiz.QuestionLocatorActivity;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4ClassRunner.class)
public class QuestionLocatorActivityTest {
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
        LocServiceFactory.setInstance(new MockLocService(testRule.getActivity()));
    }

    private String getString(int id) {
        return testRule.getActivity().getString(id);
    }
}
