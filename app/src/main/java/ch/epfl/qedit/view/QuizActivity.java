package ch.epfl.qedit.view;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import ch.epfl.qedit.R;
import ch.epfl.qedit.model.AnswerFormat;
import ch.epfl.qedit.model.Question;
import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.view.question.QuestionFragment;
import java.util.Arrays;

public class QuizActivity extends AppCompatActivity {

    private final Quiz quiz =
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
        Bundle bundle = new Bundle();
        bundle.putSerializable("question", quiz.getQuestions().get(0)); // TODO Check index
        QuestionFragment frag = new QuestionFragment();
        frag.setArguments(bundle);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.question_fragment_container, frag)
                .commitNow();
    }
}
