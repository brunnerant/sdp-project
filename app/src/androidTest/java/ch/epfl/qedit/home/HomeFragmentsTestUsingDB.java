package ch.epfl.qedit.home;

import static ch.epfl.qedit.view.LoginActivity.USER;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import ch.epfl.qedit.backend.database.DatabaseFactory;
import ch.epfl.qedit.backend.database.MockDBService;
import ch.epfl.qedit.model.User;
import com.android21buttons.fragmenttestrule.FragmentTestRule;

class HomeFragmentsTestUsingDB {
    private IdlingResource idlingResource;

    public void setup(FragmentTestRule testRule, Fragment fragment) {

        User user = new User("Jon", "Snow", User.Role.Participant);
        user.addQuiz("quiz0", "Qualification EPFL");

        Bundle bundle = new Bundle();
        bundle.putSerializable(USER, user);
        fragment.setArguments(bundle);

        MockDBService dbService = new MockDBService();
        idlingResource = dbService.getIdlingResource();
        DatabaseFactory.setInstance(dbService);
        IdlingRegistry.getInstance().register(idlingResource);

        testRule.launchFragment(fragment);
    }

    public void cleanup(
            FragmentTestRule<
                            ? extends androidx.fragment.app.FragmentActivity,
                            ch.epfl.qedit.view.home.HomeInfoFragment>
                    testRule) {
        testRule.finishActivity();
        IdlingRegistry.getInstance().unregister(idlingResource);
    }
}
