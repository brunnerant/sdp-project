package ch.epfl.qedit.Online;

import androidx.test.espresso.intent.Intents;

import com.android21buttons.fragmenttestrule.FragmentTestRule;

import org.junit.Before;
import org.junit.Rule;

import ch.epfl.qedit.R;
import ch.epfl.qedit.view.online.OnlineFragment;
import ch.epfl.qedit.view.home.HomeQuizListFragment;

public class OnlineFragmentTest {
    @Rule
    public final FragmentTestRule<?, OnlineFragment> testRule =
            FragmentTestRule.create(OnlineFragment.class, false, false);

    public OnlineFragmentTest() {
        super(R.id.home_quiz_list);
    }

    @Before
    public void setup() {
        Intents.init();
        setup(testRule, new HomeQuizListFragment());
    }
}
