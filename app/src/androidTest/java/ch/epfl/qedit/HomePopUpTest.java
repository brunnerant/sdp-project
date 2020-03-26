package ch.epfl.qedit;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.InstrumentationRegistry;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.rule.ActivityTestRule;

import com.android21buttons.fragmenttestrule.FragmentTestRule;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import ch.epfl.qedit.model.User;
import ch.epfl.qedit.view.LoginActivity;
import ch.epfl.qedit.view.home.HomeActivity;
import ch.epfl.qedit.view.home.HomePopUp;
import ch.epfl.qedit.view.home.HomeQuizListFragment;
import ch.epfl.qedit.view.quiz.QuestionFragment;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.instanceOf;

public class HomePopUpTest {
    @Rule
    public final IntentsTestRule<HomeActivity> rule =
            new IntentsTestRule<>(HomeActivity.class, false, true);

    @Test
    public void testCreationOfPopUp() {
        User user = createUser();
        launchActivity(user);
        RecyclerView.Adapter adapter = new HomeQuizListFragment().new CustomAdapter(rule.getActivity());

        // Test if no exception is thrown
        Assert.assertThat(new HomePopUp(rule.getActivity(),  user, adapter), instanceOf(HomePopUp.class));

        finishActivity();
    }

    @Test
    public void testEdit() {
        final User user = createUser();
        launchActivity(user);
        final RecyclerView.Adapter adapter = new HomeQuizListFragment().new CustomAdapter(rule.getActivity());


        rule.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                HomePopUp homePopUp = new HomePopUp(rule.getActivity(),  user, adapter);
                homePopUp.popUpEdit("", 1);

                // Check it exists
                onView(withText("Cancel"));
                onView(withText("Done"));
            }
        });

        finishActivity();
    }

    @Test
    public void testDelete() {
        final User user = createUser();
        launchActivity(user);
        final RecyclerView.Adapter adapter = new HomeQuizListFragment().new CustomAdapter(rule.getActivity());


        rule.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                HomePopUp homePopUp = new HomePopUp(rule.getActivity(),  user, adapter);
                homePopUp.popUpWarningDelete("", 2);

                // Check it exists
                onView(withText("Cancel")).check(matches(isDisplayed()));
                onView(withText("Yes")).check(matches(isDisplayed()));
            }
        });

        finishActivity();
    }

    // No @Before because it has a parameter
    public void launchActivity(User user) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable(LoginActivity.USER, user);
        intent.putExtras(bundle);
        rule.launchActivity(intent);
    }

    public void finishActivity() {
        rule.finishActivity();
    }

    private User createUser() {
        User user = new User("Role", "Editor", User.Role.Editor);
        user.addQuiz("quiz0", "Qualification EPFL");
        user.addQuiz("quiz1", "Quizz 1");
        user.addQuiz("quiz2", "Quizz 2");
        user.addQuiz("quiz3", "Quizz 3");
        user.addQuiz("quiz4", "Quizz 4");
        user.addQuiz("quiz5", "Quizz 5");
        user.addQuiz("quiz6", "Quizz 6");
        return user;
    }
}
