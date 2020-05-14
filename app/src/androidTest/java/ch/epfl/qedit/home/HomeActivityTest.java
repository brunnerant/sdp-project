package ch.epfl.qedit.home;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.contrib.DrawerMatchers.isOpen;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static ch.epfl.qedit.util.Util.clickOn;
import static ch.epfl.qedit.util.Util.isDisplayed;
import static ch.epfl.qedit.util.Util.onDialog;
import static ch.epfl.qedit.view.edit.EditQuizSettingsDialog.QUIZ_BUILDER;
import static ch.epfl.qedit.view.home.HomeQuizListFragment.STRING_POOL;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import androidx.test.runner.lifecycle.Stage;
import ch.epfl.qedit.R;
import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.model.StringPool;
import ch.epfl.qedit.model.User;
import ch.epfl.qedit.util.RecyclerViewHelpers;
import ch.epfl.qedit.view.LoginActivity;
import ch.epfl.qedit.view.edit.EditQuizActivity;
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
        User user = new User("Marcel", "Doe");
        user.addQuiz("quiz0", "Qualification EPFL");

        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable(LoginActivity.USER, user);
        intent.putExtras(bundle);
        testRule.launchActivity(intent);
        // always close the statistics panel by default to be sure it does not take to much place on
        // a little screen
        clickOn(R.id.display_stats, false);
    }

    @After
    public void finishActivity() {
        testRule.finishActivity();
    }

    @Test
    public void testInfoIsDisplayed() {
        isDisplayed(R.id.home_info_container, false);
    }

    @Test
    public void testQuizListIsDisplayed() {
        isDisplayed(R.id.home_quiz_list_container, false);
    }

    public void assertEditTextError(int id) {
        onDialog(android.R.id.button1).check(matches(not(isEnabled())));
        onDialog(R.id.edit_quiz_title)
                .check(matches(hasErrorText(testRule.getActivity().getString(id))));
    }

    @Test
    public void testAddItem() {
        clickOn(R.id.add, false);

        onView(withText(testRule.getActivity().getString(R.string.edit_dialog_title_settings)))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));
        onDialog(R.id.edit_quiz_title).check(matches(isDisplayed()));
        onDialog(R.id.treasure_hunt_checkbox).check(matches((isDisplayed())));
        onDialog(R.id.edit_language_selection).check(matches((isDisplayed())));

        // Test that an empty name is not allowed
        assertEditTextError(R.string.empty_quiz_name_error);
        closeSoftKeyboard();

        // Test that a duplicate name is not allowed either
        onDialog(R.id.edit_quiz_title).perform(typeText("Qualification EPFL"));
        closeSoftKeyboard();
        assertEditTextError(R.string.dup_quiz_name_error);

        // Test that a regular name is allowed
        onDialog(R.id.edit_quiz_title).perform(clearText());
        onDialog(R.id.edit_quiz_title).perform(typeText("New quiz"));
        closeSoftKeyboard();

        clickOn(android.R.id.button1, false);
        intended(
                allOf(
                        hasComponent(EditQuizActivity.class.getName()),
                        hasExtra(equalTo(STRING_POOL), instanceOf(StringPool.class)),
                        hasExtra(equalTo(QUIZ_BUILDER), instanceOf(Quiz.Builder.class))));
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
        onView(withId(R.id.burger_view))
                .perform(NavigationViewActions.navigateTo(R.id.online_quizzes));
        onView(withId(R.id.burger_view)).perform(NavigationViewActions.navigateTo(R.id.my_account));
        onView(withId(R.id.burger_view))
                .perform(NavigationViewActions.navigateTo(R.id.qr_code_burger));
        onView(withId(R.id.burger_view)).perform(NavigationViewActions.navigateTo(R.id.settings));
        onView(withId(R.id.drawer_layout))
                .check(matches(isOpen(Gravity.START)))
                .perform(DrawerActions.close());
    }

    private Activity currentActivity = null;

    public Activity getActivityInstance() {
        getInstrumentation()
                .runOnMainSync(
                        () -> {
                            Collection<Activity> resumedActivities =
                                    ActivityLifecycleMonitorRegistry.getInstance()
                                            .getActivitiesInStage(Stage.RESUMED);
                            for (Activity activity : resumedActivities) {
                                Log.d("Your current activity: ", activity.getClass().getName());
                                currentActivity = activity;
                                break;
                            }
                        });
        return currentActivity;
    }
}
