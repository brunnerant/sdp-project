package ch.epfl.qedit;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

import android.content.Intent;
import android.os.Bundle;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import ch.epfl.qedit.model.User;
import ch.epfl.qedit.view.HomeActivity;
import ch.epfl.qedit.view.LoginActivity;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.qedit.R;

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

    public void finishActivity() {
        testRule.finishActivity();
    }

    public void testUserIsDisplayedCorrectly(User user, String greeting, String role) {
        launchActivity(user);
        onView(withId(R.id.greeting)).check(matches(withText(greeting)));
        onView(withId(R.id.role)).check(matches(withText(role)));
        finishActivity();
    }
// TODO tests hardcoded strings https://developer.android.com/training/testing/unit-testing/instrumented-unit-tests
    /*
    @Test
    public void testParticipantIsDisplayedCorrectly() {
        
        testUserIsDisplayedCorrectly(
                new User("Bill", "Gates", User.Role.Participant),
                getResources().getString(R.string.welcome)
                        + "Bill Gates"
                        + getInstrumentation().getContext().getResources().getString(R.string.exclamation_point),
                getResources().getString(R.string.role_participant));
    }
    */
/*
    @Test
    public void testEditorIsDisplayedCorrectly() {
        testUserIsDisplayedCorrectly(
                new User("John", "Cena", User.Role.Editor),
                "Bienvenue John Cena !",
                "Vous êtes un éditeur.");
    }

    @Test
    public void testAdministratorIsDisplayedCorrectly() {
        testUserIsDisplayedCorrectly(
                new User("The", "Rock", User.Role.Administrator),
                "Bienvenue The Rock !",
                "Vous êtes un administrateur.");
    }

    @Test
    public void testGoToQuiz() {
        launchActivity(new User("The", "Rock", User.Role.Administrator));
        onView(withId(R.id.quiz_button)).perform(click());
        intended(hasComponent(QuizActivity.class.getName()));
        finishActivity();
    }

 */
}
