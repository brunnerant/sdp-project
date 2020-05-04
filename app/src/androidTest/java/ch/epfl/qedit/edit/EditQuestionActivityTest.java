package ch.epfl.qedit.edit;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.ActivityResultMatchers.hasResultCode;
import static androidx.test.espresso.contrib.ActivityResultMatchers.hasResultData;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.qedit.util.Util.clickOn;
import static ch.epfl.qedit.util.Util.isDisplayed;
import static ch.epfl.qedit.util.Util.onDialog;
import static ch.epfl.qedit.view.edit.EditNewQuizSettingsActivity.STRING_POOL;
import static ch.epfl.qedit.view.edit.EditOverviewFragment.QUESTION;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

import android.content.Intent;
import android.os.Bundle;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;
import ch.epfl.qedit.R;
import ch.epfl.qedit.model.Question;
import ch.epfl.qedit.model.StringPool;
import ch.epfl.qedit.view.edit.EditFieldFragment;
import ch.epfl.qedit.view.edit.EditQuestionActivity;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class EditQuestionActivityTest {
    @Rule
    public final ActivityTestRule<EditQuestionActivity> testRule =
            new ActivityTestRule<>(EditQuestionActivity.class, false, false);

    @Before
    public void setUp() {
        Intents.init();
        Question question = new Question.Empty();
        StringPool stringPool = new StringPool();

        Intent intent;
        intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable(QUESTION, question);
        bundle.putSerializable(STRING_POOL, stringPool);
        intent.putExtras(bundle);

        testRule.launchActivity(intent);
    }

    @After
    public void cleanUp() {
        testRule.finishActivity();
        Intents.release();
    }

    private void openFieldEdition(int buttonId) {

        clickOn(buttonId, true);

        int titleId =
                testRule.getActivity().getResources().getIdentifier("alertTitle", "id", "android");

        onDialog(titleId)
                .check(matches(withText(R.string.edit_field_title)))
                .check(matches(ViewMatchers.isDisplayed()));
    }

    private void checkTypeInSpinner(int typeIdx) {
        String type =
                testRule.getActivity()
                        .getResources()
                        .getStringArray(R.array.field_types_list)[typeIdx];
        onDialog(R.id.field_types_selection).check(matches(withSpinnerText(containsString(type))));
    }

    @Test
    public void textButtonOnClick() {
        openFieldEdition(R.id.text_button);
        checkTypeInSpinner(EditFieldFragment.TEXT_TYPE_IDX);
    }

    @Test
    public void numberButtonOnClick() {
        openFieldEdition(R.id.number_button);
        checkTypeInSpinner(EditFieldFragment.NUMBER_TYPE_IDX);
    }

    @Test
    public void buttonAreDisplay() {
        isDisplayed(R.id.text_button, true);
        isDisplayed(R.id.number_button, true);
        isDisplayed(R.id.button_done_question_editing, true);
        isDisplayed(R.id.button_cancel_question_editing, true);
    }

    @Test
    public void testReturnResultCancel() {
        clickOn(R.id.button_cancel_question_editing, true);

        assertThat(testRule.getActivityResult(), hasResultCode(RESULT_CANCELED));
        assertThat(
                testRule.getActivityResult(),
                not(hasResultData(IntentMatchers.hasExtraWithKey(QUESTION))));
        assertThat(
                testRule.getActivityResult(),
                not(hasResultData(IntentMatchers.hasExtraWithKey(STRING_POOL))));
    }

    @Test
    public void testReturnResult() {
        // answer
        openFieldEdition(R.id.text_button);
        onDialog(R.id.field_solution).perform(typeText("A"));
        onView(withText(R.string.done)).inRoot(isDialog()).perform(click());

        // text and title
        onView(withId(R.id.edit_question_title))
                .perform(scrollTo())
                .perform(typeText("Title"))
                .perform(closeSoftKeyboard());
        onView(withId(R.id.edit_question_text))
                .perform(scrollTo())
                .perform(typeText("Text"))
                .perform(closeSoftKeyboard());

        clickOn(R.id.button_done_question_editing, true);

        assertThat(testRule.getActivityResult(), hasResultCode(RESULT_OK));
        assertThat(
                testRule.getActivityResult(),
                hasResultData(IntentMatchers.hasExtraWithKey(QUESTION)));
        assertThat(
                testRule.getActivityResult(),
                hasResultData(IntentMatchers.hasExtraWithKey(STRING_POOL)));
    }

    private void hasErrorEmpty(int id) {
        String errorMsg = testRule.getActivity().getString(R.string.cannot_be_empty);
        onView(withId(id)).perform(scrollTo()).check(matches(hasErrorText(errorMsg)));
    }

    @Test
    public void testReturnResultFail() {
        clickOn(R.id.button_done_question_editing, true);
        hasErrorEmpty(R.id.edit_question_title);
        hasErrorEmpty(R.id.edit_question_text);
    }
}
