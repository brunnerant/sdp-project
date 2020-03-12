package ch.epfl.qedit.view;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import ch.epfl.qedit.R;
import ch.epfl.qedit.model.AnswerFormat;
import ch.epfl.qedit.model.Question;
import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.view.question.QuestionFragment;
import ch.epfl.qedit.view.question.QuizOverviewFragment;
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
                                    new AnswerFormat.NumberField(0, 1, 5)),
                            new Question(
                                    2,
                                    "Question 2 test",
                                    "Is this question 2 working?",
                                    new AnswerFormat.NumberField(0, 1, 5))));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_activity);

        // Check if the quiz is non empty
        if (quiz.getNbOfQuestions() > 0) {
            // Start the QuizOverviewFragment
            Bundle bundle = new Bundle();
            bundle.putSerializable("quiz", quiz);
            QuizOverviewFragment quizOverviewFragment = new QuizOverviewFragment();
            quizOverviewFragment.setArguments(bundle);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.quiz_overview_frame, quizOverviewFragment);

            // Start QuestionFragment with the first question
            bundle = new Bundle();
            bundle.putSerializable("question", quiz.getQuestions().get(0));
            QuestionFragment questionFragment = new QuestionFragment();
            questionFragment.setArguments(bundle);
            ft.add(R.id.question_frame, questionFragment);
            ft.commit();
        } else {
            // Inform the user that the Quiz is empty
            Toast toast =
                    Toast.makeText(
                            getApplicationContext(),
                            getResources().getString(R.string.empty_quiz_error_message),
                            Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
