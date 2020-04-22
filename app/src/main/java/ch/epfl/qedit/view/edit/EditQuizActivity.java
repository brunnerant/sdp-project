package ch.epfl.qedit.view.edit;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import ch.epfl.qedit.R;
import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.view.quiz.QuestionFragment;
import ch.epfl.qedit.viewmodel.QuizViewModel;

public class EditQuizActivity extends AppCompatActivity {
    public final static String QUESTION_BUILDER= "ch.epfl.qedit.view.edit.QUESTION_BUILDER";

    private QuizViewModel model;
    private Quiz quiz;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.quiz_overview_container, new EditOverviewFragment())
                .replace(R.id.question_details_container, new QuestionFragment())
                .commit();
    }
}
