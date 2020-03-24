package ch.epfl.qedit.question;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;

import android.os.Bundle;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import ch.epfl.qedit.R;
import ch.epfl.qedit.backend.database.DatabaseFactory;
import ch.epfl.qedit.backend.database.MockDBService;
import ch.epfl.qedit.model.Question;
import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.view.quiz.QuizOverviewFragment;
import com.android21buttons.fragmenttestrule.FragmentTestRule;
import java.util.Arrays;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4ClassRunner.class)
public class QuizOverviewFragmentTest {

    private IdlingResource idlingResource;

    @Rule
    public final FragmentTestRule<?, QuizOverviewFragment> testRule =
            FragmentTestRule.create(QuizOverviewFragment.class, false, false);

    @Before
    public void init() {
        MockDBService dbService = new MockDBService();
        idlingResource = dbService.getIdlingResource();
        IdlingRegistry.getInstance().register(idlingResource);
        DatabaseFactory.setInstance(dbService);

        Quiz quiz =
                new Quiz(
                        "QuizOverviewFragmentTest",
                        Arrays.asList(
                                new Question(
                                        "The matches problem",
                                        "How many matches can fit in a shoe of size 43 ?",
                                        "matrix3x3"),
                                new Question(
                                        "Pigeons",
                                        "How many pigeons are there on Earth ? (Hint: do not count yourself)",
                                        "matrix1x1"),
                                new Question("KitchenBu", "Oyster", "matrix1x1"),
                                new Question(
                                        "Everything",
                                        "What is the answer to life the univere and everything ?",
                                        "matrix3x3"),
                                new Question(
                                        "Banane", "Combien y a-t-il de bananes ?", "matrix1x1")));

        Bundle bundle = new Bundle();
        bundle.putSerializable("quiz", quiz);

        QuizOverviewFragment quizOverviewFragment = new QuizOverviewFragment();
        quizOverviewFragment.setArguments(bundle);

        testRule.launchFragment(quizOverviewFragment);
    }

    @After
    public void cleanup() {
        IdlingRegistry.getInstance().unregister(idlingResource);
    }

    @Test
    public void testOverviewIsLoading() {
        onView(withId(R.id.question_list)).check(matches(isDisplayed()));
        // .check(matches(not(hasDescendant(any(View.class))))); TODO
    }

    @Test
    public void testQuizIsProperlyLoaded() {
        onData(anything())
                .inAdapterView(withId(R.id.question_list))
                .atPosition(0)
                .check(matches(withText("1) The matches problem")));
    }
}
