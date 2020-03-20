package ch.epfl.qedit.home;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;

import android.os.Bundle;
import ch.epfl.qedit.R;
import ch.epfl.qedit.model.User;
import ch.epfl.qedit.view.home.HomeInfoFragment;
import ch.epfl.qedit.view.home.HomeQuizListFragment;
import ch.epfl.qedit.view.quiz.QuizActivity;
import com.android21buttons.fragmenttestrule.FragmentTestRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class HomeQuizListFragmentTest {
    @Rule
    public final FragmentTestRule<?, HomeQuizListFragment> testRule =
            FragmentTestRule.create(HomeQuizListFragment.class);

    @Before
    public void init() {
        User user = new User("Marcel", "Doe", User.Role.Participant);
        user.addQuiz("quiz0", "Qualification EPFL");

        Bundle bundle = new Bundle();
        bundle.putSerializable("user", user);
        HomeInfoFragment homeInfoFragment = new HomeInfoFragment();
        homeInfoFragment.setArguments(bundle);
    }

    @After
    public void cleanup() {
        testRule.finishActivity();
    }

    @Test
    public void testQuizListIsProperlyLoaded() {
        onData(anything())
                .inAdapterView(withId(R.id.home_quiz_list))
                .atPosition(0)
                .check(matches(withText("Qualification EPFL")));
    }

    @Test
    public void testClickOnQuizLaunchesQuizActivity() {
        onData(anything())
                .inAdapterView(withId(R.id.home_quiz_list))
                .atPosition(0)
                .perform(click());
        intended(
                allOf(
                        hasComponent(QuizActivity.class.getName()),
                        hasExtra(HomeQuizListFragment.QUIZID, "quiz0")));
    }
}
