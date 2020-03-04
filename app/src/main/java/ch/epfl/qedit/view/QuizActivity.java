package ch.epfl.qedit.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import ch.epfl.qedit.R;
import ch.epfl.qedit.model.AnswerFormat;
import ch.epfl.qedit.model.Question;
import ch.epfl.qedit.view.question.QuestionFragment;

public class QuizActivity extends AppCompatActivity {

    private Question question = new Question(0, "Question test", "Is this question working?", new AnswerFormat.NumberField(0, 1, 5));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_activity);
        if (savedInstanceState == null) {
            // Passe the first question as argument to the new QuestionFragment
            Bundle bundle = new Bundle();
            bundle.putSerializable("q0", question);
            QuestionFragment frag = QuestionFragment.newInstance();
            frag.setArguments(bundle);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, frag)
                    .commitNow();
        }
    }
}
