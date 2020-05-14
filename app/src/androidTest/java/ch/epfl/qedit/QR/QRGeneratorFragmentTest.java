package ch.epfl.qedit.QR;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static ch.epfl.qedit.view.QR.QRGeneratorFragment.QUIZ_ID;

import android.os.Bundle;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import ch.epfl.qedit.R;
import ch.epfl.qedit.view.QR.QRGeneratorFragment;
import com.android21buttons.fragmenttestrule.FragmentTestRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4ClassRunner.class)
public class QRGeneratorFragmentTest {
    @Rule
    public final FragmentTestRule<?, QRGeneratorFragment> testRule =
            FragmentTestRule.create(QRGeneratorFragment.class, false, false);

    @Before
    public void init() {
        String quizName = "Quiz 0";
        Bundle bundle = new Bundle();
        bundle.putSerializable(QUIZ_ID, quizName);
        QRGeneratorFragment qrGeneratorFragment = new QRGeneratorFragment();
        qrGeneratorFragment.setArguments(bundle);
        testRule.launchFragment(qrGeneratorFragment);
    }

    @After
    public void cleanup() {
        testRule.finishActivity();
    }

    @Test
    public void testQRDisplayed() {
        onView(withId(R.id.qr_code)).check(matches(isDisplayed()));
    }
}
