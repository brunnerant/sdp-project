package ch.epfl.qedit.home;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4ClassRunner.class)
public class HomeQuizListFragmentTest { // extends HomeFragmentsTestUsingDB {
    //    @Rule
    //    public final FragmentTestRule<?, HomeQuizListFragment> testRule =
    //            FragmentTestRule.create(HomeQuizListFragment.class, false, false);
    //
    //    @Before
    //    public void setup() {
    //        Intents.init();
    //        setup(testRule, new HomeQuizListFragment());
    //    }
    //
    //    @After
    //    public void cleanup() {
    //        Intents.release();
    //        cleanup(testRule);
    //    }
    //
    @Test
    public void testQuizListIsProperlyLoaded() {
        // onData(anything())
        //      .inAdapterView(withId(R.id.home_quiz_list))
        //    .atPosition(0)
        //  .check(matches(withText("Qualification EPFL")));
    }
    //
    //    @Test
    //    public void testClickOnQuizLaunchesQuizActivity() {
    //        onData(anything())
    //                .inAdapterView(withId(R.id.home_quiz_list))
    //                .atPosition(0)
    //                .perform(click());
    //        intended(
    //                allOf(
    //                        hasComponent(QuizActivity.class.getName()),
    //                        hasExtra(equalTo(HomeQuizListFragment.QUIZ_ID),
    // instanceOf(Quiz.class))));
    //    }
}
