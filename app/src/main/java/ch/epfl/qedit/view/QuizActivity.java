package ch.epfl.qedit.view;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import ch.epfl.qedit.R;
import ch.epfl.qedit.view.question.QuestionFragment;
import ch.epfl.qedit.view.question.QuizOverviewFragment;
import ch.epfl.qedit.viewmodel.QuizViewModel;

public class QuizActivity extends AppCompatActivity {
    private QuizViewModel model;
    private Boolean overviewActive;

    private ProgressBar progressBar;
    private QuizOverviewFragment overview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_activity);
        progressBar = findViewById(R.id.quiz_progress_bar);

        model = new ViewModelProvider(this).get(QuizViewModel.class);
        model.loadQuiz();

        getSupportFragmentManager()
                .beginTransaction()
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
        overview = new QuizOverviewFragment();
        overviewActive = false;
    }

    /** This handles the loading status of the quiz */
    private void onStatusChanged(QuizViewModel.Status status) {
        if (status == QuizViewModel.Status.Loading) progressBar.setVisibility(View.VISIBLE);
        else progressBar.setVisibility(View.GONE);

        if (status == QuizViewModel.Status.CouldntLoad)
            Toast.makeText(
                    getApplicationContext(), R.string.connection_error_message, Toast.LENGTH_SHORT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.next:
            case R.id.previous:
                handleNavigation(id == R.id.next ? 1 : -1);
                break;
            case R.id.time:
                Toast.makeText(this, "Unimplemented Feature", Toast.LENGTH_SHORT).show();
                break;
            case R.id.overview:
                handleToggleOverview();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return true;
    }

    /** This function handles navigating back and forth between questions */
    private void handleNavigation(int offset) {
        // If the quiz hasn't loaded yet, we cannot navigate
        if (model.getQuiz().getValue() == null) return;

        MutableLiveData<Integer> focusedQuestion = model.getFocusedQuestion();
        Integer index = focusedQuestion.getValue();

        if (index == null) focusedQuestion.setValue(0);
        else if ((index + offset) < model.getQuiz().getValue().getQuestions().size()
                && (index + offset) >= 0) focusedQuestion.setValue(index + offset);
    }

    /** This function handles toggling the overview fragment */
    private void handleToggleOverview() {
        if (!overviewActive) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.quiz_overview_container, overview)
                    .commit();
            findViewById(R.id.quiz_overview_container).setVisibility(View.VISIBLE);
            overviewActive = true;
        } else {
            getSupportFragmentManager().beginTransaction().remove(overview).commit();
            findViewById(R.id.quiz_overview_container).setVisibility(View.GONE);
            overviewActive = false;
        }
    }
}
