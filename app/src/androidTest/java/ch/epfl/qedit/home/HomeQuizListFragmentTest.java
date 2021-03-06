package ch.epfl.qedit.home;

import static android.app.Activity.RESULT_OK;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.qedit.util.Util.createTestQuiz;
import static ch.epfl.qedit.util.Util.createTestStringPool;
import static ch.epfl.qedit.util.Util.onDialog;
import static ch.epfl.qedit.view.edit.EditQuizSettingsDialog.QUIZ_BUILDER;
import static ch.epfl.qedit.view.home.HomeActivity.USER;
import static ch.epfl.qedit.view.home.HomeQuizListFragment.EDIT_NEW_QUIZ_REQUEST_CODE;
import static ch.epfl.qedit.view.home.HomeQuizListFragment.QUIZ_ID;
import static ch.epfl.qedit.view.home.HomeQuizListFragment.STRING_POOL;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.IsNot.not;

import android.app.Instrumentation;
import android.content.Intent;
import android.os.Bundle;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.intent.Intents;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.ActivityTestRule;
import ch.epfl.qedit.R;
import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.model.StringPool;
import ch.epfl.qedit.model.User;
import ch.epfl.qedit.view.edit.EditQuizActivity;
import ch.epfl.qedit.view.home.HomeActivity;
import ch.epfl.qedit.view.home.HomeQuizListFragment;
import ch.epfl.qedit.view.quiz.QuizActivity;
import ch.epfl.qedit.view.treasurehunt.TreasureHuntActivity;
import com.android21buttons.fragmenttestrule.FragmentTestRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

// Note that this test doesn't test the behaviour of the recycler is already tested
// in another test
@RunWith(AndroidJUnit4ClassRunner.class)
public class HomeQuizListFragmentTest extends HomeFragmentsTestUsingDB {
    @Rule
    public final FragmentTestRule<?, HomeQuizListFragment> testRule =
            FragmentTestRule.create(HomeQuizListFragment.class, false, false);

    @Rule
    public final ActivityTestRule<HomeActivity> resultTestRule =
            new ActivityTestRule<>(HomeActivity.class, false, false);

    public HomeQuizListFragmentTest() {
        super(R.id.home_quiz_list);
    }

    @Before
    public void setup() {
        Intents.init();
        setup(testRule, new HomeQuizListFragment());
    }

    @After
    public void cleanup() {
        Intents.release();
        cleanup(testRule);
    }

    @Test
    public void testQuizListIsProperlyLoaded() {
        itemView(0, android.R.id.text1).check(matches(withText("I am a Mock Quiz!")));
    }

    @Test
    public void testDeleteItem() {
        itemView(0, R.id.list_item_three_dots).perform(click());
        clickOnPopup(testRule.getActivity(), R.string.menu_delete);

        // Check that the confirmation dialog is displayed
        onView(withText(testRule.getActivity().getString(R.string.warning_delete_quiz)))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

        // Cancel, and see if the item is still present
        onView(withId(android.R.id.button2)).perform(click());
        item(0).check(matches(isDisplayed()));

        // Now delete for real and see if it was deleted
        itemView(0, R.id.list_item_three_dots).perform(click());
        clickOnPopup(testRule.getActivity(), R.string.menu_delete);

        onView(withId(android.R.id.button1)).perform(click());
        onView(withText("I am a Mock Quiz!")).check(doesNotExist());
    }

    // Edition of a existing quiz dialog tests

    @Test
    public void testClickOnEditLaunchesDialogModify() {
        itemView(0, R.id.list_item_three_dots).perform(click());
        clickOnPopup(testRule.getActivity(), R.string.menu_edit);

        onView(withText(testRule.getActivity().getString(R.string.edit_dialog_title_settings)))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));
        onDialog(R.id.edit_quiz_title).check(matches(isDisplayed()));
        onDialog(R.id.treasure_hunt_checkbox).check(matches(not(isDisplayed())));
        onDialog(R.id.edit_language_selection).check(matches(not(isDisplayed())));
    }

    @Test
    public void testEnterTitle() {
        itemView(0, R.id.list_item_three_dots).perform(click());
        clickOnPopup(testRule.getActivity(), R.string.menu_edit);

        onDialog(R.id.edit_quiz_title).check(matches(withText("I am a Mock Quiz!")));

        onDialog(R.id.edit_quiz_title).perform(clearText());
        onDialog(R.id.edit_quiz_title).perform(typeText("New Title"));
        Espresso.closeSoftKeyboard();
        onDialog(R.id.edit_quiz_title).check(matches(withText("New Title")));
    }

    @Test
    public void testDoneButton() {
        itemView(0, R.id.list_item_three_dots).perform(click());
        clickOnPopup(testRule.getActivity(), R.string.menu_edit);

        onDialog(android.R.id.button1).perform(click());
        intended(
                allOf(
                        hasComponent(EditQuizActivity.class.getName()),
                        hasExtra(equalTo(STRING_POOL), instanceOf(StringPool.class)),
                        hasExtra(equalTo(QUIZ_BUILDER), instanceOf(Quiz.Builder.class))));
    }

    @Test
    public void testParticipateToNormalQuiz() {
        item(0).perform(click());
        intended(
                allOf(
                        hasComponent(QuizActivity.class.getName()),
                        hasExtra(equalTo(QUIZ_ID), instanceOf(Quiz.class))));
    }

    @Test
    public void testParticipateToTreasureHunt() {
        item(1).perform(click());
        intended(
                allOf(
                        hasComponent(TreasureHuntActivity.class.getName()),
                        hasExtra(equalTo(QUIZ_ID), instanceOf(Quiz.class))));
    }

    @Test
    public void testGenerateQRCode() {
        itemView(0, R.id.list_item_three_dots).perform(click());
        clickOnPopup(testRule.getActivity(), R.string.menu_qr);

        onView(withId(R.id.qr_image)).check(matches(isDisplayed()));
        onView(withId(R.id.qr_close)).perform(click());
    }

    public void testOnActivityResult() {
        User user = new User("Marcel", "Doe");
        user.addQuiz("quiz0", "I am a Mock Quiz!");

        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable(USER, user);
        intent.putExtras(bundle);

        Quiz quiz = createTestQuiz();
        StringPool stringPool = createTestStringPool("Test");

        Intent dataIntent = new Intent();
        dataIntent.putExtra(QUIZ_ID, quiz);
        dataIntent.putExtra(STRING_POOL, stringPool);

        resultTestRule.launchActivity(intent);

        intending(hasComponent(EditQuizActivity.class.getName()))
                .respondWith(new Instrumentation.ActivityResult(RESULT_OK, dataIntent));
        resultTestRule
                .getActivity()
                .startActivityForResult(
                        new Intent(resultTestRule.getActivity(), EditQuizActivity.class),
                        EDIT_NEW_QUIZ_REQUEST_CODE);

        resultTestRule.finishActivity();
    }
}
