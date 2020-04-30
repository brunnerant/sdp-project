package ch.epfl.qedit.edit;

import static android.app.Activity.RESULT_OK;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.contrib.ActivityResultMatchers.hasResultCode;
import static androidx.test.espresso.contrib.ActivityResultMatchers.hasResultData;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static ch.epfl.qedit.view.edit.EditNewQuizSettingsActivity.STRING_POOL;
import static ch.epfl.qedit.view.edit.EditOverviewFragment.QUESTION_BUILDER;
import static ch.epfl.qedit.view.edit.EditQuestionActivity.QUESTION;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.AllOf.allOf;

import android.content.Intent;
import android.os.Bundle;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.rule.ActivityTestRule;
import ch.epfl.qedit.R;
import ch.epfl.qedit.model.Question;
import ch.epfl.qedit.model.StringPool;
import ch.epfl.qedit.view.edit.EditAnswerActivity;
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
        Question.Builder questionBuilder = new Question.Builder();
        StringPool stringPool = new StringPool();

        Intent intent;
        intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable(QUESTION_BUILDER, questionBuilder);
        bundle.putSerializable(STRING_POOL, stringPool);
        intent.putExtras(bundle);

        testRule.launchActivity(intent);
    }

    @After
    public void cleanUp() {
        testRule.finishActivity();
        Intents.release();
    }

    @Test
    public void testAddAnswerButton() {
        onView(withId(R.id.add_button)).perform(click());
        intended(allOf(hasComponent(EditAnswerActivity.class.getName())));
    }

    @Test
    public void testReturnResult() {
        onView(withId(R.id.button_done_editing)).perform(click());

        assertThat(testRule.getActivityResult(), hasResultCode(RESULT_OK));
        assertThat(
                testRule.getActivityResult(),
                hasResultData(IntentMatchers.hasExtraWithKey(QUESTION)));
        assertThat(
                testRule.getActivityResult(),
                hasResultData(IntentMatchers.hasExtraWithKey(STRING_POOL)));
    }
}
