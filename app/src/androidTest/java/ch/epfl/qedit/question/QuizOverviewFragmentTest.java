package ch.epfl.qedit.question;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.not;

import android.view.View;
import androidx.lifecycle.ViewModelProvider;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import ch.epfl.qedit.R;
import ch.epfl.qedit.backend.database.DatabaseFactory;
import ch.epfl.qedit.backend.database.MockDBService;
import ch.epfl.qedit.view.quiz.QuizOverviewFragment;
import ch.epfl.qedit.viewmodel.QuizViewModel;
import com.android21buttons.fragmenttestrule.FragmentTestRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4ClassRunner.class)
public class QuizOverviewFragmentTest {

    private IdlingResource idlingResource;
    private QuizViewModel model;

    @Rule
    public final FragmentTestRule<?, QuizOverviewFragment> testRule =
            FragmentTestRule.create(QuizOverviewFragment.class);

    @Before
    public void init() {
        MockDBService dbService = new MockDBService();
        idlingResource = dbService.getIdlingResource();
        IdlingRegistry.getInstance().register(idlingResource);
        DatabaseFactory.setInstance(dbService);
        model = new ViewModelProvider(testRule.getActivity()).get(QuizViewModel.class);
    }

    @After
    public void cleanup() {
        IdlingRegistry.getInstance().unregister(idlingResource);
    }

    @Test
    public void testOverviewIsLoading() {
        onView(withId(R.id.question_list))
                .check(matches(isDisplayed()))
                .check(matches(not(hasDescendant(any(View.class)))));
    }

    @Test
    public void testQuizIsProperlyLoaded() {
        model.loadQuestions("quiz0");
        onData(anything())
                .inAdapterView(withId(R.id.question_list))
                .atPosition(0)
                .check(matches(withText("1) Banane")));
    }
}
