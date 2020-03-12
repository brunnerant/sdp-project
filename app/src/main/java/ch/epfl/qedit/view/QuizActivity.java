package ch.epfl.qedit.view;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import ch.epfl.qedit.R;
import ch.epfl.qedit.model.AnswerFormat;
import ch.epfl.qedit.model.Question;
import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.view.question.QuestionFragment;
import java.util.Arrays;

public class QuizActivity extends AppCompatActivity {

    private Quiz quiz =
            new Quiz(
                    Arrays.asList(
                            new Question(
                                    0,
                                    "Question 0 test",
                                    "Is this question 0 working?",
                                    new AnswerFormat.NumberField(0, 1, 5)),
                            new Question(
                                    1,
                                    "Question 1 test",
                                    "Is this question 1 working?",
                                    new AnswerFormat.NumberField(0, 1, 5))));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_activity);

        // Pass the first question as argument to the new QuestionFragment
        if (quiz.getNbOfQuestions() > 0) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("question", quiz.getQuestions().get(0));
            QuestionFragment frag = new QuestionFragment();
            frag.setArguments(bundle);

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.question_fragment_container, frag)
                    .commitNow();
        } else {
            Toast toast =
                    Toast.makeText(
                            getApplicationContext(),
                            getResources().getString(R.string.empty_quiz_error_message),
                            Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
