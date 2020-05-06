package ch.epfl.qedit.edit;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static ch.epfl.qedit.view.edit.EditSettingsActivity.QUIZ_BUILDER;
import static ch.epfl.qedit.view.edit.EditSettingsActivity.STRING_POOL;

import android.content.Intent;
import android.os.Bundle;
import androidx.test.rule.ActivityTestRule;
import ch.epfl.qedit.R;
import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.model.StringPool;
import ch.epfl.qedit.view.edit.EditQuizActivity;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class EditQuizActivityTest {
    @Rule
    public final ActivityTestRule<EditQuizActivity> testRule =
            new ActivityTestRule<>(EditQuizActivity.class, false, false);

    @Before
    public void setUp() {
        Quiz.Builder quizBuilder = new Quiz.Builder();
        StringPool stringPool = new StringPool();

        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable(QUIZ_BUILDER, quizBuilder);
        bundle.putSerializable(STRING_POOL, stringPool);
        intent.putExtras(bundle);

        testRule.launchActivity(intent);
    }

    @After
    public void cleanUp() {
        testRule.finishActivity();
    }

    @Test
    public void testThatFragmentsAreDisplayed() {
        onView(withId(R.id.quiz_overview_container)).check(matches(isDisplayed()));
        onView(withId(R.id.question_details_container)).check(matches(isDisplayed()));
    }
}
