package ch.epfl.qedit.view.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import ch.epfl.qedit.R;
import ch.epfl.qedit.view.home.HomeQuizListFragment;
import ch.epfl.qedit.viewmodel.QuizViewModel;
import java.util.Objects;

public class QuizActivity extends AppCompatActivity {
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        progressBar = findViewById(R.id.quiz_progress_bar);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String quizID =
                (String)
                        Objects.requireNonNull(intent.getExtras())
                                .getSerializable(HomeQuizListFragment.QUIZID);

        QuizViewModel model = new ViewModelProvider(this).get(QuizViewModel.class);
        model.loadQuiz(quizID);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.quiz_overview_container, new QuizOverviewFragment())
                .replace(R.id.question_details_container, new QuestionFragment())
                .commit();

        model.getStatus()
                .observe(
                        this,
                        new Observer<QuizViewModel.Status>() {
                            @Override
                            public void onChanged(QuizViewModel.Status status) {
                                onStatusChanged(status);
                            }
                        });
    }

    /** This handles the loading status of the quiz */
    private void onStatusChanged(QuizViewModel.Status status) {
        if (status == QuizViewModel.Status.Loading) progressBar.setVisibility(View.VISIBLE);
        else progressBar.setVisibility(View.GONE);

        if (status == QuizViewModel.Status.CouldNotLoad)
            Toast.makeText(
                            getApplicationContext(),
                            R.string.connection_error_message,
                            Toast.LENGTH_SHORT)
                    .show();
    }
}
