package ch.epfl.qedit.edit;

import static androidx.test.espresso.action.ViewActions.click;
import static ch.epfl.qedit.util.Util.onDialog;

import ch.epfl.qedit.R;
import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.util.RecyclerViewHelpers;

public class EditTest extends RecyclerViewHelpers {
    protected EditTest() {
        super(R.id.question_list);
    }

    public void emptyQuizList(Quiz quiz) {
        for (int i = 0; i < quiz.getQuestions().size(); ++i) {
            item(0).perform(click());
            itemView(0, R.id.delete_button).perform(click());
            onDialog(android.R.id.button1).perform(click());
        }
    }
}
