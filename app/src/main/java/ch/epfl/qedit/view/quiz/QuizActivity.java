package ch.epfl.qedit.view.quiz;

import android.content.Intent;
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
import ch.epfl.qedit.view.home.HomeQuizListFragment;
import ch.epfl.qedit.viewmodel.QuizViewModel;
import java.util.Objects;

public class QuizActivity extends AppCompatActivity {
    private QuizViewModel model;
    private Boolean overviewActive;

    private ProgressBar progressBar;
    private QuizOverviewFragment overview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String quizID =
                (String)
                        Objects.requireNonNull(intent.getExtras())
                                .getSerializable(HomeQuizListFragment.QUIZID);

        progressBar = findViewById(R.id.quiz_progress_bar);
        model = new ViewModelProvider(this).get(QuizViewModel.class);
        model.getStatus()
                .observe(
                        this,
                        new Observer<QuizViewModel.Status>() {
                            @Override
                            public void onChanged(QuizViewModel.Status status) {
                                onStatusChanged(status);
                            }
                        });
        model.loadQuestions(quizID);

        overview = new QuizOverviewFragment();
        overviewActive = false;
        handleToggleOverview();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.question_details_container, new QuestionFragment())
                .commit();
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
