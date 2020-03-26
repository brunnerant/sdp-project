package ch.epfl.qedit;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.instanceOf;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import ch.epfl.qedit.model.User;
import ch.epfl.qedit.view.LoginActivity;
import ch.epfl.qedit.view.home.HomeActivity;
import ch.epfl.qedit.view.home.HomePopUp;
import ch.epfl.qedit.view.home.HomeQuizListFragment;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

public class HomePopUpTest {
    @Rule
    public final IntentsTestRule<HomeActivity> rule =
            new IntentsTestRule<>(HomeActivity.class, false, false);

    @Test
    public void testCreationOfPopUp() {
        final User user = createUser();
        launchActivity();
        RecyclerView.Adapter adapter =
                new HomeQuizListFragment().new CustomAdapter(rule.getActivity());

        // Test if no exception is thrown
        Assert.assertThat(
                new HomePopUp(rule.getActivity(), user, adapter), instanceOf(HomePopUp.class));

        finishActivity();
    }

    @Test
    public void testEdit() {
        final User user = createUser();
        launchActivity();

        rule.getActivity()
                .runOnUiThread(
                        new Runnable() {
                            public void run() {
                                final RecyclerView.Adapter adapter =
                                        new HomeQuizListFragment()
                                        .new CustomAdapter(rule.getActivity());

                                final HomePopUp homePopUp =
                                        new HomePopUp(rule.getActivity(), user, adapter);
                                final AlertDialog alertDialog = homePopUp.popUpEdit("", 1);
                                alertDialog.show();

                                // Check it exists
                                onView(withText("Cancel"));
                                onView(withText("Done"));

                                alertDialog.cancel();
                            }
                        });

        finishActivity();
    }

    @Test
    public void testDelete() {
        final User user = launchActivity();
        final RecyclerView.Adapter adapter =
                new HomeQuizListFragment().new CustomAdapter(rule.getActivity());
        new Handler(Looper.getMainLooper())
                .post(
                        new Runnable() {
                            @Override
                            public void run() {

                                HomePopUp homePopUp =
                                        new HomePopUp(rule.getActivity(), user, adapter);
                                final AlertDialog alertDialog = homePopUp.popUpWarningDelete("", 2);
                                alertDialog.show();
                                // Check it exists
                                onView(withText("Cancel"));
                                onView(withText("Yes"));
                                alertDialog.cancel();
                            }
                        });

        finishActivity();
    }

    // No @Before because it has a parameter
    public User launchActivity() {
        User user = createUser();
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable(LoginActivity.USER, user);
        intent.putExtras(bundle);
        rule.launchActivity(intent);
        return user;
    }

    public void finishActivity() {
        rule.finishActivity();
    }

    private User createUser() {
        User user = new User("Role", "Editor", User.Role.Editor);
        user.addQuiz("quiz0", "Qualification EPFL");
        user.addQuiz("quiz1", "qual EPFL");
        return user;
    }
}
