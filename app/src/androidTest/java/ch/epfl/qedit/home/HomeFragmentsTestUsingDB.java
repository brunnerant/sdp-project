// package ch.epfl.qedit.home;
//
// import static ch.epfl.qedit.view.LoginActivity.USER;
//
// import android.os.Bundle;
// import androidx.fragment.app.Fragment;
// import ch.epfl.qedit.model.User;
// import com.android21buttons.fragmenttestrule.FragmentTestRule;
//
// public class HomeFragmentsTestUsingDB {
//    private FragmentTestRule testRule;
//
//    public void setup(FragmentTestRule testRule, Fragment fragment) {
//        this.testRule = testRule;
//
//        User user = new User("Jon", "Snow", User.Role.Participant);
//        user.addQuiz("quiz0", "Qualification EPFL");
//
//        Bundle bundle = new Bundle();
//        bundle.putSerializable(USER, user);
//        fragment.setArguments(bundle);
//
//        testRule.launchFragment(fragment);
//    }
//
//    public void cleanup() {
//        testRule.finishActivity();
//    }
// }

package ch.epfl.qedit.home; // TODO

import static ch.epfl.qedit.view.LoginActivity.USER;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import ch.epfl.qedit.model.User;
import com.android21buttons.fragmenttestrule.FragmentTestRule;

public class HomeFragmentsTestUsingDB {
    private FragmentTestRule testRule;

    public void setup(FragmentTestRule testRule, Fragment fragment) {
        this.testRule = testRule;

        User user = new User("Jon", "Snow", User.Role.Participant);
        user.addQuiz("quiz0", "Qualification EPFL");

        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable(USER, user);
        intent.putExtras(bundle);

        testRule.launchActivity(intent);

        fragment.setArguments(bundle);

        // testRule.launchFragment(fragment);
    }

    public void cleanup() {
        testRule.finishActivity();
    }
}
