package ch.epfl.qedit.edit;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.qedit.util.Util.clickOn;
import static ch.epfl.qedit.util.Util.isDisplayed;
import static ch.epfl.qedit.util.Util.onDialog;
import static org.hamcrest.Matchers.containsString;

import android.content.Intent;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;
import ch.epfl.qedit.R;
import ch.epfl.qedit.view.edit.EditAnswerActivity;
import ch.epfl.qedit.view.edit.EditFieldFragment;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class EditAnswerActivityTest {
    @Rule
    public final ActivityTestRule<EditAnswerActivity> testRule =
            new ActivityTestRule<>(EditAnswerActivity.class, false, false);

    @Before
    public void init() {
        Intent intent = new Intent();
        testRule.launchActivity(intent);
    }

    @After
    public void cleanUp() {
        testRule.finishActivity();
    }

    private String[] getFieldTypes() {
        return testRule.getActivity().getResources().getStringArray(R.array.field_types_list);
    }

    @Test
    public void helperTextIsDisplay() {
        isDisplayed(R.id.choose_answer_text, true);
    }

    @Test
    public void buttonAreDisplay() {
        isDisplayed(R.id.text_button, true);
        isDisplayed(R.id.graph_button, true);
        isDisplayed(R.id.number_button, true);
        isDisplayed(R.id.matrix_button, true);
    }

    @Test
    public void buttonOnClick() {
        // just print a toast for now
        clickOn(R.id.graph_button, true);
        clickOn(R.id.matrix_button, true);
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
        onDialog(R.id.field_types_selection)
                .check(matches(withSpinnerText(containsString(getFieldTypes()[typeIdx]))));
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
}
