package ch.epfl.qedit.home;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.contrib.DrawerMatchers.isOpen;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.Matchers.not;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import androidx.test.runner.lifecycle.Stage;
import ch.epfl.qedit.R;
import ch.epfl.qedit.model.User;
import ch.epfl.qedit.util.RecyclerViewHelpers;
import ch.epfl.qedit.view.LoginActivity;
import ch.epfl.qedit.view.home.HomeActivity;
import java.util.Collection;
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
        bundle.putSerializable(LoginActivity.USER, user);
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
        closeSoftKeyboard();

        // Test that a duplicate name is not allowed either
        onView(withId(R.id.quiz_name_text)).perform(typeText("Qualification EPFL"));
        closeSoftKeyboard();
        assertEditTextError(R.string.dup_quiz_name_error);

        // Test that a regular name is allowed
        onView(withId(R.id.quiz_name_text)).perform(clearText());
        onView(withId(R.id.quiz_name_text)).perform(typeText("New quiz"));
        closeSoftKeyboard();

        onView(withId(android.R.id.button1)).perform(click());
        itemView(1, android.R.id.text1).check(matches(withText("New quiz")));
    }

    @Test
    public void testOpenCloseDrawer() {
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.START)))
                .perform(DrawerActions.open());

        onView(withId(R.id.drawer_layout)).check(matches(isOpen()));
        onView(withId(R.id.drawer_layout))
                .check(matches(isOpen(Gravity.START)))
                .perform(DrawerActions.close());

        onView(withId(R.id.drawer_layout)).check(matches(isClosed(Gravity.START)));
    }

    @Test
    public void testClickHome() {
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.START)))
                .perform(DrawerActions.open());
        onView(withId(R.id.burger_view))
                .perform(NavigationViewActions.navigateTo(R.id.burger_home));
    }

    @Test
    public void testSelectUnimplementedFeatures() {
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.START)))
                .perform(DrawerActions.open());
        onView(withId(R.id.burger_view)).perform(NavigationViewActions.navigateTo(R.id.my_quizzes));
        onView(withId(R.id.burger_view)).perform(NavigationViewActions.navigateTo(R.id.my_account));
        onView(withId(R.id.burger_view))
                .perform(NavigationViewActions.navigateTo(R.id.qr_code_burger));
        onView(withId(R.id.burger_view)).perform(NavigationViewActions.navigateTo(R.id.settings));
        onView(withId(R.id.drawer_layout))
                .check(matches(isOpen(Gravity.START)))
                .perform(DrawerActions.close());
    }

    Activity currentActivity = null;

    public Activity getActivityInstance() {
        getInstrumentation()
                .runOnMainSync(
                        new Runnable() {
                            public void run() {
                                Collection<Activity> resumedActivities =
                                        ActivityLifecycleMonitorRegistry.getInstance()
                                                .getActivitiesInStage(Stage.RESUMED);
                                for (Activity activity : resumedActivities) {
                                    Log.d("Your current activity: ", activity.getClass().getName());
                                    currentActivity = activity;
                                    break;
                                }
                            }
                        });
        return currentActivity;
    }
}
