package ch.epfl.qedit.edit;

import static androidx.test.espresso.action.ViewActions.click;
import static ch.epfl.qedit.util.Util.onDialog;

import androidx.test.rule.ActivityTestRule;
import ch.epfl.qedit.R;
import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.util.RecyclerViewHelpers;

public class EditTest extends RecyclerViewHelpers {
    protected EditTest() {
        super(R.id.question_list);
    }

    public void emptyQuizList(Quiz quiz, ActivityTestRule testRule) {
        for (int i = 0; i < quiz.getQuestions().size(); ++i) {
            itemView(0, R.id.list_item_three_dots).perform(click());
            clickOnPopup(testRule.getActivity(), R.string.menu_delete);
            onDialog(android.R.id.button1).perform(click());
        }
    }
}
