package ch.epfl.qedit.view;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
        Toolbar toolbar = findViewById(R.id.quizToolbar);
        setSupportActionBar(toolbar);
        overview = new QuizOverviewFragment();
        overViewActive = false;
    }

    // Check if the quiz is non empty
    /*  if (quiz.getQuestions().size() > 0) {
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
        questionFragment = new QuestionFragment();
        questionFragment.setArguments(bundle);
        ft.add(R.id.question_frame, questionFragment);
        ft.commit();
    } else { TODO
          // Inform the user that the Quiz is empty
          Toast toast =
                  Toast.makeText(
                          getApplicationContext(),
                          getResources().getString(R.string.empty_quiz_error_message),
                          Toast.LENGTH_SHORT);
          toast.show();
      }*/

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

        if (item.getItemId() == R.id.next || item.getItemId() == R.id.previous) {

            int temp = item.getItemId() == R.id.next ? 1 : -1;
            if (model.getFocusedQuestion().getValue() == null) {
                model.getFocusedQuestion().setValue(0);
            } else if ((model.getFocusedQuestion().getValue() + temp)
                            < model.getQuiz().getValue().getQuestions().size()
                    && (model.getFocusedQuestion().getValue() + temp) >= 0) {
                model.getFocusedQuestion().setValue(model.getFocusedQuestion().getValue() + temp);
            }

        } else if (item.getItemId() == R.id.time) {
            /*TODO
            display the quiz timer*/
            Toast.makeText(this, "Unimplemented Feature", Toast.LENGTH_SHORT).show();
        } else if (item.getItemId() == R.id.overview) {
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
        }
        return true;
    }
}
