package ch.epfl.qedit;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Intent;
import android.os.Bundle;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import ch.epfl.qedit.model.User;
import ch.epfl.qedit.view.HomeActivity;
import ch.epfl.qedit.view.LoginActivity;
import ch.epfl.qedit.view.QuizActivity;
import java.util.function.Function;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4ClassRunner.class)
public class HomeActivityTest {
    @Rule
    public final IntentsTestRule<HomeActivity> testRule =
            new IntentsTestRule<>(HomeActivity.class, false, false);

    public void launchActivity(User user) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable(LoginActivity.USER, user);
        intent.putExtras(bundle);
        testRule.launchActivity(intent);
    }

    @After
    public void finishActivity() {
        testRule.finishActivity();
    }

    public void testUserIsDisplayedCorrectly(
            String firstName,
            String lastName,
            User.Role role,
            Function<HomeActivity, String> roleString) {
        User user = new User(firstName, lastName, role);
        launchActivity(user);
        String greetingString =
                testRule.getActivity().getString(R.string.welcome)
                        + " "
                        + firstName
                        + " "
                        + lastName
                        + testRule.getActivity().getString(R.string.exclamation_point);
        onView(withId(R.id.greeting)).check(matches(withText(greetingString)));
        onView(withId(R.id.role))
                .check(matches(withText(roleString.apply(testRule.getActivity()))));
        finishActivity();
    }

    @Test
    public void testParticipantIsDisplayedCorrectly() {
        testUserIsDisplayedCorrectly(
                "Bill",
                "Gates",
                User.Role.Participant,
                new Function<HomeActivity, String>() {
                    @Override
                    public String apply(HomeActivity homeActivity) {
                        return homeActivity.getString(R.string.role_participant);
                    }
                });
    }

    @Test
    public void testEditorIsDisplayedCorrectly() {
        testUserIsDisplayedCorrectly(
                "John",
                "Cena",
                User.Role.Editor,
                new Function<HomeActivity, String>() {
                    @Override
                    public String apply(HomeActivity homeActivity) {
                        return homeActivity.getString(R.string.role_editor);
                    }
                });
    }

    @Test
    public void testAdministratorIsDisplayedCorrectly() {
        testUserIsDisplayedCorrectly(
                "The",
                "Rock",
                User.Role.Administrator,
                new Function<HomeActivity, String>() {
                    @Override
                    public String apply(HomeActivity homeActivity) {
                        return homeActivity.getString(R.string.role_administrator);
                    }
                });
    }

    @Test
    public void testGoToQuiz() {
        launchActivity(new User("The", "Rock", User.Role.Administrator));
        onView(withId(R.id.quiz_button)).perform(click());
        intended(hasComponent(QuizActivity.class.getName()));
        finishActivity();
    }
}
