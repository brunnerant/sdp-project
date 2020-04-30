package ch.epfl.qedit.edit;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.qedit.view.edit.EditFieldFragment.NUMBER_TYPE_IDX;
import static ch.epfl.qedit.view.edit.EditFieldFragment.PRE_FILLED_TYPE_IDX;
import static ch.epfl.qedit.view.edit.EditFieldFragment.TEXT_TYPE_IDX;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.IsNot.not;

import android.content.Intent;
import androidx.test.rule.ActivityTestRule;
import ch.epfl.qedit.R;
import ch.epfl.qedit.view.edit.EditAnswerActivity;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class EditFieldFragmentTest {
    @Rule
    public final ActivityTestRule<EditAnswerActivity> testRule =
            new ActivityTestRule<>(EditAnswerActivity.class, false, false);

    @Before
    public void init() {
        Intent intent = new Intent();
        testRule.launchActivity(intent);
        onView(withId(R.id.text_button)).perform(click());
    }

    @After
    public void cleanUp() {
        testRule.finishActivity();
    }

    private String[] getFieldTypes() {
        return testRule.getActivity().getResources().getStringArray(R.array.field_types_list);
    }

    private void changeType(int idx) {
        int spinnerId = R.id.field_types_selection;
        String nb = getFieldTypes()[idx];
        onView(withId(spinnerId)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is(nb)))
                .inRoot(isPlatformPopup())
                .perform(click());
        onView(withId(spinnerId)).check(matches(withSpinnerText(containsString(nb))));
    }

    private void checkHint(String hint) {
        onView(withId(R.id.field_preview)).inRoot(isDialog()).check(matches(withHint(hint)));
    }

    @Test
    public void titlesAreDisplay() {
        onView(withId(R.id.field_preview_title))
                .inRoot(isDialog())
                .check(matches(withText(R.string.preview_title)))
                .check(matches(isDisplayed()));

        onView(withId(R.id.selectTypeText))
                .inRoot(isDialog())
                .check(matches(withText(R.string.select_field_type)))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testChangeType() {

        int spinnerId = R.id.field_types_selection;
        onView(withId(spinnerId))
                .inRoot(isDialog())
                .check(matches(withSpinnerText(containsString(getFieldTypes()[TEXT_TYPE_IDX]))));

        onView(withId(R.id.field_preview)).inRoot(isDialog()).check(matches(withHint("???")));

        changeType(NUMBER_TYPE_IDX);

        onView(withId(R.id.field_preview)).inRoot(isDialog()).check(matches(withHint("0")));

        changeType(PRE_FILLED_TYPE_IDX);

        onView(withId(R.id.field_preview))
                .inRoot(isDialog())
                .check(matches(withHint(R.string.hint_pre_filled_field)));
    }

    @Test
    public void testCheckboxDisplay() {
        changeType(PRE_FILLED_TYPE_IDX);

        onView(withId(R.id.decimalCheckBox)).inRoot(isDialog()).check(matches(not(isDisplayed())));

        onView(withId(R.id.signCheckBox)).inRoot(isDialog()).check(matches(not(isDisplayed())));

        changeType(NUMBER_TYPE_IDX);

        onView(withId(R.id.decimalCheckBox)).inRoot(isDialog()).check(matches(isDisplayed()));

        onView(withId(R.id.signCheckBox)).inRoot(isDialog()).check(matches(isDisplayed()));
    }

    @Test
    public void testNumberType() {
        changeType(NUMBER_TYPE_IDX);

        onView(withId(R.id.decimalCheckBox)).inRoot(isDialog()).perform(click());

        checkHint("0.0");

        onView(withId(R.id.signCheckBox)).inRoot(isDialog()).perform(click());

        checkHint("±0.0");

        onView(withId(R.id.decimalCheckBox)).inRoot(isDialog()).perform(click());

        checkHint("±0");
    }
}
