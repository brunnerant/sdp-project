package ch.epfl.qedit.home;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

import android.content.Intent;
import android.os.Bundle;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import ch.epfl.qedit.R;
import ch.epfl.qedit.model.User;
import ch.epfl.qedit.util.RecyclerViewHelpers;
import ch.epfl.qedit.view.home.HomeActivity;
import ch.epfl.qedit.view.login.TokenLogInActivity;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4ClassRunner.class)
public class HomeActivityTest extends RecyclerViewHelpers {
    @Rule
    public final IntentsTestRule<HomeActivity> testRule =
            new IntentsTestRule<>(HomeActivity.class, false, false);

    public HomeActivityTest() {
        super(R.id.home_quiz_list);
    }

    @Before
    public void launchActivity() {
        User user = new User("Marcel", "Doe", User.Role.Participant);
        user.addQuiz("quiz0", "Qualification EPFL");

        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable(TokenLogInActivity.USER, user);
        intent.putExtras(bundle);
        testRule.launchActivity(intent);
    }

    @After
    public void finishActivity() {
        testRule.finishActivity();
    }

    @Test
    public void testInfoIsDisplayed() {
        onView(ViewMatchers.withId(R.id.home_info_container)).check(matches(isDisplayed()));
    }

    @Test
    public void testQuizListIsDisplayed() {
        onView(withId(R.id.home_quiz_list_container)).check(matches(isDisplayed()));
    }

    public void assertEditTextError(int id) {
        onView(withId(android.R.id.button1)).check(matches(not(isEnabled())));
        onView(withId(R.id.quiz_name_text))
                .check(matches(hasErrorText(testRule.getActivity().getString(id))));
    }

    @Test
    public void testAddItem() {
        onView(withId(R.id.add)).perform(click());
        onView(withText(testRule.getActivity().getString(R.string.add_quiz_message)))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

        // Test that an empty name is not allowed
        assertEditTextError(R.string.empty_quiz_name_error);

        // Test that a duplicate name is not allowed either
        onView(withId(R.id.quiz_name_text)).perform(typeText("Qualification EPFL"));
        assertEditTextError(R.string.dup_quiz_name_error);

        // Test that a regular name is allowed
        onView(withId(R.id.quiz_name_text)).perform(clearText());
        onView(withId(R.id.quiz_name_text)).perform(typeText("New quiz"));
        onView(withId(android.R.id.button1)).perform(click());
        itemView(1, android.R.id.text1).check(matches(withText("New quiz")));
    }
}
