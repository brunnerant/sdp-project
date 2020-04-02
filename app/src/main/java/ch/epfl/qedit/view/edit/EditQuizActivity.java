package ch.epfl.qedit.view.edit;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import ch.epfl.qedit.R;
import ch.epfl.qedit.model.Question;
import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.view.quiz.QuestionFragment;
import ch.epfl.qedit.viewmodel.QuizViewModel;
import java.util.Arrays;
import java.util.List;

public class EditQuizActivity extends AppCompatActivity {
    private QuizViewModel model;
    private Quiz quiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        tempQuiz();
        // For now, we inflate the quiz edit overview fragment
        model = new ViewModelProvider(this).get(QuizViewModel.class);
        model.setQuiz(quiz);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.quiz_overview_container, new EditOverviewFragment())
                .replace(R.id.question_details_container, new QuestionFragment())
                .commit();
    }

    // TODO: Delete when we connect the rest of the app
    private void tempQuiz() {
        List<Question> q_en =
                Arrays.asList(
                        new Question(
                                "Bananas", "How many bananas are there on Earth?!", "matrix1x1"),
                        new Question("Apples", "How many apples are there on Earth?!", "matrix1x1"),
                        new Question("Vectors", "Give a unit vector.", "matrix1x3"),
                        new Question("Operations", "What is the results of 1 + 10?!", "matrix1x1"),
                        new Question("Matrices", "Fill those matrix !", "matrix3x3"));

        quiz = new Quiz("I am a Mock Quiz!", q_en);
    }
}
