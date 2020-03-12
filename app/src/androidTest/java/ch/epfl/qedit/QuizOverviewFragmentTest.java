package ch.epfl.qedit;

import android.app.Activity;
import android.content.Intent;
import android.widget.ListView;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.qedit.view.QuizActivity;

import static androidx.core.content.ContextCompat.startActivity;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class QuizOverviewFragmentTest {

    @Rule
    public final ActivityTestRule<QuizActivity> mActivityRule =
            new ActivityTestRule<>(QuizActivity.class);

    @Test
    public void testOverviewButtons() {
/*
        final ListView listView = (ListView) QuizActivity.findViewById(R.id.quiz_overview_frame);
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                listView.performItemClick(listView, 0, listView.getItemIdAtPosition(0));
            }
        });
*/
        QuizActivity quizActivity = new QuizActivity();
        final ListView listView = (ListView) quizActivity.findViewById(R.id.quiz_overview_frame);
        listView.performItemClick(listView, 0, listView.getItemIdAtPosition(0));

/*
        onView(withId(R.id.quiz_overview_frame)).perform(click());
        onView(withId(R.id.question_title)).check(matches(withText("1) The matches problem")));
*/

        /*
        listView = (ListView) activity.findViewById(android.R.id.list);
        child0 = listView.getChildAt(0); // returns null!
        child0.performClick(); // throws a NullPointerException!
        */
/*
        listView = (ListView) activity.findViewById(android.R.id.quiz_overview_frame);
        long itemId = listView.getAdapter().getItemId(0);

        listView.performItemClick(child0, 0, itemId); // throws a NullPointerException!

        */
/*

        final ListView list = (ListView) mActivity.findViewById(R.id.listId);
        assertNotNull("The list was not loaded", list);
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {

                list.performItemClick(list.getAdapter().getView(position, null, null),
                        position, list.getAdapter().getItemId(position));
            }

        });

        getInstrumentation().waitForIdleSync();

        mFragment frag = mActivity.getFragment();
        assertNotNull("Fragment was not loaded", frag);
        */
 
    }

}
