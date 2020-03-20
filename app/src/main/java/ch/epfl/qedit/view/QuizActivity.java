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
    private Boolean overViewActive;

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
        overViewActive = false;
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
        MutableLiveData<Integer> FocusedQuestion = model.getFocusedQuestion();
        Integer index = FocusedQuestion.getValue();
        switch (id) {
            case R.id.next:
            case R.id.previous:
                int temp = id == R.id.next ? 1 : -1;
                if (index == null) {
                    FocusedQuestion.setValue(0);
                } else if ((index + temp) < model.getQuiz().getValue().getQuestions().size()
                        && (index + temp) >= 0) {
                    FocusedQuestion.setValue((index + temp));
                }
                break;
            case R.id.time:
                Toast.makeText(this, "Unimplemented Feature", Toast.LENGTH_SHORT).show();
                break;
            case R.id.overview:
                if (!overViewActive) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.quiz_overview_container, overview)
                            .commit();
                    overViewActive = true;
                } else {
                    getSupportFragmentManager().beginTransaction().remove(overview).commit();
                    overViewActive = false;
                }
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }
}
