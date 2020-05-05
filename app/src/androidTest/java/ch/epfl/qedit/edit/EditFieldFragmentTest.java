package ch.epfl.qedit.edit;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.qedit.util.Util.clickOn;
import static ch.epfl.qedit.util.Util.inputSolutionText;
import static ch.epfl.qedit.util.Util.onDialog;
import static ch.epfl.qedit.view.edit.EditFieldFragment.NUMBER_TYPE_IDX;
import static ch.epfl.qedit.view.edit.EditFieldFragment.PRE_FILLED_TYPE_IDX;
import static ch.epfl.qedit.view.edit.EditFieldFragment.TEXT_TYPE_IDX;
import static ch.epfl.qedit.view.edit.EditNewQuizSettingsActivity.STRING_POOL;
import static ch.epfl.qedit.view.edit.EditOverviewFragment.QUESTION;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.IsNot.not;

import android.content.Intent;
import android.os.Bundle;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.intent.Intents;
import androidx.test.rule.ActivityTestRule;
import ch.epfl.qedit.R;
import ch.epfl.qedit.model.Question;
import ch.epfl.qedit.model.StringPool;
import ch.epfl.qedit.view.edit.EditQuestionActivity;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class EditFieldFragmentTest {
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
        Espresso.closeSoftKeyboard();
        clickOn(R.id.text_button, true);
        Espresso.closeSoftKeyboard();
    }

    @After
    public void cleanUp() {
        testRule.finishActivity();
        Intents.release();
    }

    private String getFieldTypes(int idx) {
        return testRule.getActivity().getResources().getStringArray(R.array.field_types_list)[idx];
    }

    private ViewInteraction onDialogScroll(int id) {
        return onDialog(id).perform(scrollTo());
    }

    private void changeType(int idx) {
        int spinnerId = R.id.field_types_selection;
        String nb = getFieldTypes(idx);
        onDialogScroll(spinnerId).perform(click());
        onData(allOf(is(instanceOf(String.class)), is(nb)))
                .inRoot(isPlatformPopup())
                .perform(scrollTo())
                .perform(click());
        onDialogScroll(spinnerId).check(matches(withSpinnerText(containsString(nb))));
    }

    private void checkSolutionHint(int hint) {
        onDialogScroll(R.id.field_solution).check(matches(withHint(hint)));
    }

    private void checkHintPreview(String hint) {
        onDialogScroll(R.id.field_hint_preview).check(matches(withText(containsString(hint))));
    }

    @Test
    public void titlesAreDisplay() {
        String hint_preview = testRule.getActivity().getString(R.string.hint_preview);
        onDialogScroll(R.id.field_hint_preview)
                .check(matches(withText(containsString(hint_preview))))
                .check(matches(isDisplayed()));

        onDialogScroll(R.id.selectTypeText)
                .check(matches(withText(R.string.select_field_type)))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testChangeType() {
        onDialogScroll(R.id.field_types_selection)
                .check(matches(withSpinnerText(containsString(getFieldTypes(TEXT_TYPE_IDX)))));
        checkSolutionHint(R.string.enter_solution);

        changeType(NUMBER_TYPE_IDX);
        checkSolutionHint(R.string.enter_solution);

        changeType(PRE_FILLED_TYPE_IDX);
        checkSolutionHint(R.string.enter_pre_filled);
    }

    @Test
    public void testCheckboxDisplay() {
        changeType(PRE_FILLED_TYPE_IDX);
        onDialog(R.id.decimalCheckBox).check(matches(not(isDisplayed())));
        onDialog(R.id.signCheckBox).check(matches(not(isDisplayed())));

        changeType(NUMBER_TYPE_IDX);
        onDialog(R.id.decimalCheckBox).perform(scrollTo()).check(matches(isDisplayed()));
        onDialog(R.id.signCheckBox).perform(scrollTo()).check(matches(isDisplayed()));
    }

    @Test
    public void testNumberType() {
        changeType(NUMBER_TYPE_IDX);
        checkHintPreview("0");

        onDialogScroll(R.id.decimalCheckBox).perform(click());
        checkHintPreview("0.0");

        onDialogScroll(R.id.signCheckBox).perform(click());
        checkHintPreview("±0.0");

        onDialogScroll(R.id.decimalCheckBox).perform(click());
        checkHintPreview("±0");
    }

    private void testResult() {
        inputSolutionText("1");
        onView(withText(R.string.done)).inRoot(isDialog()).perform(click());
    }

    @Test
    public void testReturnNumberResult() {
        changeType(NUMBER_TYPE_IDX);
        testResult();
    }

    @Test
    public void testReturnTextResult() {
        changeType(TEXT_TYPE_IDX);
        testResult();
    }

    @Test
    public void testReturnPreFilledResult() {
        changeType(PRE_FILLED_TYPE_IDX);
        testResult();
    }

    @Test
    public void testCancel() {
        onView(withText(R.string.cancel)).inRoot(isDialog()).perform(click());
    }

    @Test
    public void testDoneFail() {
        String errorMsg = testRule.getActivity().getString(R.string.cannot_be_empty);
        onView(withText(R.string.done)).inRoot(isDialog()).perform(click());
        onDialogScroll(R.id.field_solution).check(matches(hasErrorText(errorMsg)));
    }
}
