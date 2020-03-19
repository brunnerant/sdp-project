package ch.epfl.qedit;

import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import ch.epfl.qedit.view.QuizActivity;
import org.junit.Rule;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4ClassRunner.class)
public class QuizActivityTest {
    @Rule
    public final IntentsTestRule<QuizActivity> mActivityRule =
            new IntentsTestRule<>(QuizActivity.class, false, false);
}
