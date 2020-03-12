package ch.epfl.qedit.view;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import ch.epfl.qedit.R;
import ch.epfl.qedit.model.AnswerFormat;
import ch.epfl.qedit.model.Question;
import ch.epfl.qedit.view.question.QuestionFragment;
import ch.epfl.qedit.view.question.QuizOverviewFragment;

public class QuizActivity extends AppCompatActivity {

    private Question question =
            new Question(
                    0,
                    "Question test",
                    "Is this question working?",
                    new AnswerFormat.NumberField(0, 1, 5));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_activity);
        // Pass the first question as argument to the new QuestionFragment
        Bundle bundle = new Bundle();
        bundle.putSerializable("q0", question);
        QuestionFragment questionFragment = QuestionFragment.newInstance();
        questionFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction().replace(R.id.questionFrame, questionFragment).commitNow();

        // Quiz overview

/*
        Question qu0 = new Question(
                        0,
                        "Question 0",
                        "Bla",
                        new AnswerFormat.NumberField(0, 1, 5));

        Question qu1 = new Question(
                1,
                "Question 1",
                "Bla bla",
                new AnswerFormat.NumberField(0, 1, 5));

        Question qu2 = new Question(
                2,
                "Question 2",
                "Bla bla bla",
                new AnswerFormat.NumberField(0, 1, 5));


        Bundle bundle1 = new Bundle();
        bundle.putSerializable("qu0", qu0);
        bundle.putSerializable("qu1", qu1);
        bundle.putSerializable("qu2", qu2);
        QuizOverviewFragment quizOverviewFragment = new QuizOverviewFragment();
        quizOverviewFragment.setArguments(bundle1);

        getSupportFragmentManager().beginTransaction().replace(R.id.quizOverviewFrame, quizOverviewFragment).commitNow();

*/


        getSupportFragmentManager().beginTransaction().add(R.id.quizOverviewFrame, quizOverviewFragment).commit();
    }
}
