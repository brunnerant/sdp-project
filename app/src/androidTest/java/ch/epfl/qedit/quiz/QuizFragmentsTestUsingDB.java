package ch.epfl.qedit.quiz;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import ch.epfl.qedit.backend.database.DatabaseFactory;
import ch.epfl.qedit.backend.database.MockDBService;
import ch.epfl.qedit.model.Question;
import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.viewmodel.QuizViewModel;
import com.android21buttons.fragmenttestrule.FragmentTestRule;
import java.util.Arrays;

public class QuizFragmentsTestUsingDB {
    private IdlingResource idlingResource;
    private FragmentTestRule testRule;

    public QuizViewModel setup(FragmentTestRule testRule, Fragment fragment) {
        this.testRule = testRule;

        MockDBService dbService = new MockDBService();
        idlingResource = dbService.getIdlingResource();
        IdlingRegistry.getInstance().register(idlingResource);
        DatabaseFactory.setInstance(dbService);

        QuizViewModel model =
                new ViewModelProvider((ViewModelStoreOwner) testRule.getActivity())
                        .get(QuizViewModel.class);

        model.setQuiz(
                new Quiz(
                        "Test",
                        Arrays.asList(
                                new Question(
                                        "The matches problem",
                                        "How many matches can fit in a shoe of size 43 ?",
                                        "matrix3x3"),
                                new Question(
                                        "Title 2", "Test answer format", "testAnswerFormat"))));

        testRule.launchFragment(fragment);

        return model;
    }

    public void cleanup() {
        IdlingRegistry.getInstance().unregister(idlingResource);
        testRule.finishActivity();
    }
}
