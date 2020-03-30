package ch.epfl.qedit.answer;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.os.Bundle;
import androidx.lifecycle.ViewModelProvider;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import ch.epfl.qedit.backend.database.DatabaseFactory;
import ch.epfl.qedit.backend.database.MockDBService;
import ch.epfl.qedit.model.MatrixFormat;
import ch.epfl.qedit.view.answer.MatrixFragment;
import ch.epfl.qedit.viewmodel.QuizViewModel;
import com.android21buttons.fragmenttestrule.FragmentTestRule;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4ClassRunner.class)
public class MatrixFragmentTest {

    private QuizViewModel model;
    private CountDownLatch lock = new CountDownLatch(1);

    @Rule
    public final FragmentTestRule<?, MatrixFragment> testRule =
            FragmentTestRule.create(MatrixFragment.class, false, false);

    @Before
    public void init() {
        MockDBService dbService = new MockDBService();
        DatabaseFactory.setInstance(dbService);
        Bundle bundle = new Bundle();
        bundle.putSerializable("m0", MatrixFormat.createMatrix3x3());

        MatrixFragment matrixFragment = new MatrixFragment();
        matrixFragment.setArguments(bundle);
        model = new ViewModelProvider(testRule.getActivity()).get(QuizViewModel.class);
        // model.getAnswers().setValue(new HashMap<Integer, HashMap<Integer, Float>>());
        testRule.launchFragment(matrixFragment);
    }

    @After
    public void cleanup() {
        testRule.finishActivity();
    }

    private void lockWait(long time) {
        try {
            lock.await(time, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void dummyTest() {
        Assert.assertEquals(true, true);
    }

    @Test
    public void testAnswersStoredCorrectly() {
       /* HashMap<Integer,HashMap<Integer,Float>>map = model.getAnswers().getValue();
        for (int i = 0; i < 20; i++) {
            map.put(i, new HashMap<Integer, Float>());
        }
        model.getAnswers().setValue(map);*/

        model.loadQuiz("quiz0");
        model.getFocusedQuestion().postValue(0);

        int id = testRule.getFragment().getId(0, 0);

        onView(withId(id)).perform(typeText("47.3"));
        Assert.assertEquals(model.getAnswers().getValue().get(0).get(id), (Float) 47.3f);
    }
}
