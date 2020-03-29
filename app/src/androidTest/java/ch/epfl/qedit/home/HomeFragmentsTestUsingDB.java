package ch.epfl.qedit.home;

import static ch.epfl.qedit.view.LoginActivity.USER;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.test.espresso.IdlingRegistry;
import ch.epfl.qedit.backend.database.DatabaseFactory;
import ch.epfl.qedit.backend.database.MockDBService;
import ch.epfl.qedit.model.User;
import com.android21buttons.fragmenttestrule.FragmentTestRule;

class HomeFragmentsTestUsingDB {

    public static void setup(FragmentTestRule testRule, Fragment fragment) {

        User user = new User("Jon", "Snow", User.Role.Participant);
        user.addQuiz("quiz0", "Qualification EPFL");

        Bundle bundle = new Bundle();
        bundle.putSerializable(USER, user);
        fragment.setArguments(bundle);
        MockDBService db = new MockDBService();
        IdlingRegistry.getInstance().register(db.getIdlingResource());

        DatabaseFactory.setInstance(db);
        testRule.launchFragment(fragment);
    }

    public static void cleanup(FragmentTestRule testRule) {
        testRule.finishActivity();
    }
}
