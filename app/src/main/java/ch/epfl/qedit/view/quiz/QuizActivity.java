package ch.epfl.qedit.view.quiz;

import static ch.epfl.qedit.view.home.HomeQuizListFragment.QUIZ_ID;

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
import ch.epfl.qedit.model.Question;
import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.model.answer.AnswerModel;
import ch.epfl.qedit.util.LocaleHelper;
import ch.epfl.qedit.view.util.ConfirmDialog;
import ch.epfl.qedit.viewmodel.QuizViewModel;
import com.google.common.collect.ImmutableList;
import java.util.HashMap;
import java.util.Objects;

public class QuizActivity extends AppCompatActivity implements ConfirmDialog.ConfirmationListener {
    public static final String GOOD_ANSWERS = "ch.epfl.qedit.quiz.GOOD_ANSWERS";
    public static final String CORRECTION = "ch.epfl.qedit.quiz.CORRECTION";
    private QuizViewModel model;
    private Boolean overviewActive;

    private ConfirmDialog validateDialog;
    private Quiz quiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // Get the Intent that started this activity and extract the quiz
        Intent intent = getIntent();
        quiz = (Quiz) Objects.requireNonNull(intent.getExtras()).getSerializable(QUIZ_ID);

        model = new ViewModelProvider(this).get(QuizViewModel.class);
        model.setQuiz(quiz);

        overviewActive = false;
        handleToggleOverview();

        validateDialog = ConfirmDialog.create("Are you sure you want to correct this quiz ?", this);

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
        getMenuInflater().inflate(R.menu.quiz_menu, menu);
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
            case R.id.validate:
                validateDialog.show(getSupportFragmentManager(), null);
                break;
        }

        return true;
    }

    /** This function handles navigating back and forth between questions */
    private void handleNavigation(int offset) {

        MutableLiveData<Integer> focusedQuestion = model.getFocusedQuestion();
        Integer index = focusedQuestion.getValue();

        if (index == null) {
            focusedQuestion.setValue(0);
        } else if ((index + offset) < quiz.getQuestions().size() && (index + offset) >= 0) {
            focusedQuestion.setValue(index + offset);
        }
    }

    /** This function handles toggling the overview fragment */
    private void handleToggleOverview() {
        findViewById(R.id.quiz_overview_container)
                .setVisibility(overviewActive ? View.GONE : View.VISIBLE);
        findViewById(R.id.separator).setVisibility(overviewActive ? View.GONE : View.VISIBLE);
        overviewActive = !overviewActive;
    }

    @Override
    public void onConfirm(ConfirmDialog dialog) {
        ImmutableList<Question> questions = quiz.getQuestions();
        HashMap<Integer, AnswerModel> answers = model.getAnswers().getValue();
        int goodAnswers = 0;
        for (int i = 0; i < questions.size(); i++) {
            if (questions.get(i).getFormat().correct(answers.get(i))) goodAnswers++;
        }
        Toast.makeText(
                        getApplicationContext(),
                        "number of good answers = " + goodAnswers,
                        Toast.LENGTH_SHORT)
                .show();
    }
}
