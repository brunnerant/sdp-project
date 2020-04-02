package ch.epfl.qedit.view.edit;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import ch.epfl.qedit.R;
import ch.epfl.qedit.view.quiz.QuestionFragment;

public class EditQuizActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // For now, we inflate the quiz edit overview fragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.quiz_overview_container, new EditOverviewFragment())
                .replace(R.id.question_details_container, new QuestionFragment())
                .commit();
    }
}
