// package ch.epfl.qedit.quiz;
//
// import androidx.fragment.app.Fragment;
// import androidx.lifecycle.ViewModelProvider;
// import androidx.lifecycle.ViewModelStoreOwner;
// import androidx.test.espresso.IdlingRegistry;
// import androidx.test.espresso.IdlingResource;
// import ch.epfl.qedit.backend.database.DatabaseFactory;
// import ch.epfl.qedit.backend.database.MockDBService;
// import ch.epfl.qedit.util.Util;
// import ch.epfl.qedit.viewmodel.QuizViewModel;
// import com.android21buttons.fragmenttestrule.FragmentTestRule;
//
// public class QuizFragmentsTestUsingDB { //TODO
//    private IdlingResource idlingResource;
//
//    public QuizViewModel setup(FragmentTestRule testRule, Fragment fragment) {
//        MockDBService dbService = new MockDBService();
//        idlingResource = dbService.getIdlingResource();
//        IdlingRegistry.getInstance().register(idlingResource);
//        DatabaseFactory.setInstance(dbService);
//
//        QuizViewModel model =
//                new ViewModelProvider((ViewModelStoreOwner) testRule.getActivity())
//                        .get(QuizViewModel.class);
//        model.setQuiz(Util.createMockQuiz("Test"));
//
//        testRule.launchFragment(fragment);
//
//        return model;
//    }
//
//    public void cleanup() {
//        IdlingRegistry.getInstance().unregister(idlingResource);
//    }
// }
package ch.epfl.qedit.quiz;

import static ch.epfl.qedit.view.home.HomeQuizListFragment.QUIZID;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import ch.epfl.qedit.backend.database.DatabaseFactory;
import ch.epfl.qedit.backend.database.MockDBService;
import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.util.Util;
import ch.epfl.qedit.viewmodel.QuizViewModel;
import com.android21buttons.fragmenttestrule.FragmentTestRule;

public class QuizFragmentsTestUsingDB { // TODO
    private IdlingResource idlingResource;
    private FragmentTestRule testRule;

    public QuizViewModel setup(FragmentTestRule testRule, Fragment fragment) {
        this.testRule = testRule;

        MockDBService dbService = new MockDBService();
        idlingResource = dbService.getIdlingResource();
        IdlingRegistry.getInstance().register(idlingResource);
        DatabaseFactory.setInstance(dbService);

        Quiz quiz = Util.createMockQuiz("Test");

        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable(QUIZID, quiz);
        intent.putExtras(bundle);

        testRule.launchActivity(intent);

        QuizViewModel model =
                new ViewModelProvider((ViewModelStoreOwner) testRule.getActivity())
                        .get(QuizViewModel.class);

        model.setQuiz(quiz);

        fragment.setArguments(bundle);

        // testRule.launchFragment(fragment);

        return model;
    }

    public void cleanup() {
        IdlingRegistry.getInstance().unregister(idlingResource);
        testRule.finishActivity();
    }
}
