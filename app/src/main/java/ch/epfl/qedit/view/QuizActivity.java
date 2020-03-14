package ch.epfl.qedit.view;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import ch.epfl.qedit.R;
import ch.epfl.qedit.view.question.QuestionFragment;
import ch.epfl.qedit.view.question.QuizOverviewFragment;
import ch.epfl.qedit.viewmodel.QuizViewModel;

public class QuizActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_activity);

        QuizViewModel model = new ViewModelProvider(this).get(QuizViewModel.class);
        model.loadQuiz();

        final ProgressBar progressBar = findViewById(R.id.quiz_progress_bar);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.quiz_overview_container, new QuizOverviewFragment())
                .add(R.id.question_details_container, new QuestionFragment())
                .commit();

        model.getStatus()
                .observe(
                        this,
                        new Observer<QuizViewModel.Status>() {
                            @Override
                            public void onChanged(QuizViewModel.Status status) {
                                if (status == QuizViewModel.Status.Loading)
                                    progressBar.setVisibility(View.VISIBLE);
                                else progressBar.setVisibility(View.GONE);

                                if (status == QuizViewModel.Status.CouldntLoad)
                                    Toast.makeText(
                                            getApplicationContext(),
                                            R.string.connection_error_message,
                                            Toast.LENGTH_SHORT);
                            }
                        });
    }
}
