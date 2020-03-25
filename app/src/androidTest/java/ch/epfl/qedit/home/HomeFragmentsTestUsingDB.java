package ch.epfl.qedit.home;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import ch.epfl.qedit.model.User;
import com.android21buttons.fragmenttestrule.FragmentTestRule;

public class HomeFragmentsTestUsingDB {
    private FragmentTestRule testRule;

    public void setup(FragmentTestRule testRule, Fragment fragment) {
        this.testRule = testRule;

        User user = new User("Marcel", "Doe", User.Role.Participant);
        user.addQuiz("quiz0", "Qualification EPFL");

        Bundle bundle = new Bundle();
        bundle.putSerializable("user", user);
        fragment.setArguments(bundle);

        testRule.launchFragment(fragment);
    }

    public void cleanup() {
        testRule.finishActivity();
    }
}
