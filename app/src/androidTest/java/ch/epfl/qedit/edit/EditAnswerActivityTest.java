package ch.epfl.qedit.edit;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.containsString;

import android.content.Intent;
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
        onView(withId(R.id.choose_answer_text)).check(matches(isDisplayed()));
    }

    @Test
    public void buttonAreDisplay() {
        onView(withId(R.id.text_button)).check(matches(isDisplayed()));
        onView(withId(R.id.graph_button)).check(matches(isDisplayed()));
        onView(withId(R.id.number_button)).check(matches(isDisplayed()));
        onView(withId(R.id.matrix_button)).check(matches(isDisplayed()));
    }

    @Test
    public void buttonOnClick() {
        // just print a toast for now
        onView(withId(R.id.graph_button)).perform(click());
        onView(withId(R.id.matrix_button)).perform(click());
    }

    private void openFieldEdition(int buttonId) {

        onView(withId(buttonId)).perform(click());

        int titleId =
                testRule.getActivity().getResources().getIdentifier("alertTitle", "id", "android");

        onView(withId(titleId))
                .inRoot(isDialog())
                .check(matches(withText(R.string.edit_field_title)))
                .check(matches(isDisplayed()));
    }

    @Test
    public void textButtonOnClick() {
        openFieldEdition(R.id.text_button);
        onView(withId(R.id.field_types_selection))
                .inRoot(isDialog())
                .check(
                        matches(
                                withSpinnerText(
                                        containsString(
                                                getFieldTypes()[
                                                        EditFieldFragment.TEXT_TYPE_IDX]))));
    }

    @Test
    public void numberButtonOnClick() {
        openFieldEdition(R.id.number_button);

        onView(withId(R.id.field_types_selection))
                .inRoot(isDialog())
                .check(
                        matches(
                                withSpinnerText(
                                        containsString(
                                                getFieldTypes()[
                                                        EditFieldFragment.NUMBER_TYPE_IDX]))));
    }
}
