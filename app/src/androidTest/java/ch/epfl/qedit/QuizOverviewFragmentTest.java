package ch.epfl.qedit;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import ch.epfl.qedit.view.QuizActivity;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class QuizOverviewFragmentTest {

    @Rule
    public final ActivityTestRule<QuizActivity> mActivityRule =
            new ActivityTestRule<>(QuizActivity.class);

    @Test
    public void testOverviewButtons() {
        /*
                View view = inflater.inflate(R.layout.quiz_overview_fragment, container, false);
                ListView listView = view.findViewById(R.id.questionList);

                listView.performItemClick(
                        listView.getAdapter().getView(0, null, null), 0, 0);
        */
        /*
                final
                runTestOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listView.performItemClick(listView, 0, listView.getItemIdAtPosition(0));
                    }
                });
        */
        // onView(withId(R.id.quiz_overview_frame)).perform(click());

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
