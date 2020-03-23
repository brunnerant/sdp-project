package ch.epfl.qedit.view;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import ch.epfl.qedit.util.LocaleHelper;
import ch.epfl.qedit.R;
import ch.epfl.qedit.view.quiz.QuestionFragment;
import ch.epfl.qedit.view.quiz.QuizOverviewFragment;
import ch.epfl.qedit.viewmodel.QuizViewModel;

public class QuizActivity extends AppCompatActivity {
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_activity);
        progressBar = findViewById(R.id.quiz_progress_bar);

        QuizViewModel model = new ViewModelProvider(this).get(QuizViewModel.class);
        model.loadQuiz();

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

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
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
