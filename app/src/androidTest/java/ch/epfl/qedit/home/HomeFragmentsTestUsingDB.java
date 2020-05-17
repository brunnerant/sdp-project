package ch.epfl.qedit.home;

import static ch.epfl.qedit.view.login.Util.USER;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import ch.epfl.qedit.backend.database.DatabaseFactory;
import ch.epfl.qedit.backend.database.MockDBService;
import ch.epfl.qedit.model.User;
import ch.epfl.qedit.util.RecyclerViewHelpers;
import com.android21buttons.fragmenttestrule.FragmentTestRule;

class HomeFragmentsTestUsingDB extends RecyclerViewHelpers {
    private IdlingResource idlingResource;
    int score = 145, successes = 42, attempts = 78;

    HomeFragmentsTestUsingDB(int recyclerViewId) {
        super(recyclerViewId);
    }

    public void setup(FragmentTestRule testRule, Fragment fragment) {
        User user = new User("Jon", "Snow", score, successes, attempts);
        user.addQuiz("quiz0", "I am a Mock Quiz!");

        Bundle bundle = new Bundle();
        bundle.putSerializable(USER, user);
        fragment.setArguments(bundle);

        MockDBService dbService = new MockDBService();
        idlingResource = dbService.getIdlingResource();
        DatabaseFactory.setInstance(dbService);
        IdlingRegistry.getInstance().register(idlingResource);

        testRule.launchFragment(fragment);
    }

    public void cleanup(FragmentTestRule testRule) {
        testRule.finishActivity();
        IdlingRegistry.getInstance().unregister(idlingResource);
    }
}
