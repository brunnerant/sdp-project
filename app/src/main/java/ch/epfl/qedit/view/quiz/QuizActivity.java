package ch.epfl.qedit.view.quiz;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import ch.epfl.qedit.R;
import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.util.LocaleHelper;
import ch.epfl.qedit.view.home.HomeQuizListFragment;
import ch.epfl.qedit.viewmodel.QuizViewModel;
import java.util.Objects;

public class QuizActivity extends AppCompatActivity {
    private QuizViewModel model;
    private Boolean overviewActive;

    private Quiz quiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        quiz =
                (Quiz)
                        Objects.requireNonNull(intent.getExtras())
                                .getSerializable(HomeQuizListFragment.QUIZID);

        model = new ViewModelProvider(this).get(QuizViewModel.class);
        model.setQuiz(quiz);

        overviewActive = false;
        handleToggleOverview();

        QuestionFragment questionFragment = new QuestionFragment();
        QuizOverviewFragment overview = new QuizOverviewFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.question_details_container, questionFragment)
                .replace(R.id.quiz_overview_container, overview)
                .commit();
    }


    @Override
    /* This method is needed to apply the desired language at the activity startup */
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
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

        MutableLiveData<Integer> focusedQuestion = model.getFocusedQuestion();
        Integer index = focusedQuestion.getValue();

        if (index == null) focusedQuestion.setValue(0);
        else if ((index + offset) < quiz.getQuestions().size() && (index + offset) >= 0)
            focusedQuestion.setValue(index + offset);
    }

    /** This function handles toggling the overview fragment */
    private void handleToggleOverview() {
        findViewById(R.id.quiz_overview_container)
                .setVisibility(overviewActive ? View.GONE : View.VISIBLE);
        overviewActive = !overviewActive;
    }
}
