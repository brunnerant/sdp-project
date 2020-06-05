package ch.epfl.qedit.view.quiz;

import static ch.epfl.qedit.view.home.HomeActivity.USER;
import static ch.epfl.qedit.view.home.HomeQuizListFragment.QUIZ_ID;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import ch.epfl.qedit.R;
import ch.epfl.qedit.model.Question;
import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.model.User;
import ch.epfl.qedit.model.answer.AnswerModel;
import ch.epfl.qedit.model.answer.MatrixFormat;
import ch.epfl.qedit.model.answer.MatrixModel;
import ch.epfl.qedit.util.LocaleHelper;
import ch.epfl.qedit.view.home.HomeActivity;
import ch.epfl.qedit.view.util.ConfirmDialog;
import ch.epfl.qedit.viewmodel.QuizViewModel;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class QuizActivity extends AppCompatActivity implements ConfirmDialog.ConfirmationListener {
    public static final String PARTICIPANT_ANSWERS = "ch.epfl.qedit.quiz.PARTICIPANT_ANSWERS";
    public static final String QUESTIONS = "ch.epfl.qedit.quiz.QUESTIONS";
    public static final String GOOD_ANSWERS = "ch.epfl.qedit.quiz.GOOD_ANSWERS";
    public static final String CORRECTION = "ch.epfl.qedit.quiz.CORRECTION";
    private QuizViewModel model;
    private Boolean overviewActive;

    private User user;
    private ConfirmDialog validateDialog;
    private Quiz quiz;
    private Boolean correction;
    private ArrayList<Integer> correctedQuestions;
    private ArrayList<Integer> goodAnswers = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // Get the Intent that started this activity and extract the quiz
        Intent intent = getIntent();
        quiz = (Quiz) Objects.requireNonNull(intent.getExtras()).getSerializable(QUIZ_ID);
        user = (User) Objects.requireNonNull(intent.getExtras().getSerializable(USER));

        correction = (Boolean) Objects.requireNonNull(intent.getExtras().getBoolean(CORRECTION));
        goodAnswers = (ArrayList<Integer>) intent.getExtras().getSerializable(GOOD_ANSWERS);

        model = new ViewModelProvider(this).get(QuizViewModel.class);
        model.setQuiz(quiz);
        correctedQuestions = new ArrayList<>();
        overviewActive = false;
        handleToggleOverview();
        QuestionFragment questionFragment = new QuestionFragment();
        QuizOverviewFragment overview = new QuizOverviewFragment();

        Bundle bundle = new Bundle();
        bundle.putIntegerArrayList(GOOD_ANSWERS, goodAnswers);
        questionFragment.setArguments(bundle);
        instantiateFragments(correction, questionFragment, overview);
    }

    private void instantiateFragments(
            Boolean correcting, QuestionFragment qFragment, QuizOverviewFragment overviewFragment) {

        if (correcting) {
            int nbOfGoodAnswers = Collections.frequency(goodAnswers, 1);
            float ratio = nbOfGoodAnswers / goodAnswers.size();
            user.incrementScore(nbOfGoodAnswers);
            if (ratio >= 0.8) {
                user.incrementSuccess();
            } else {
                user.incrementAttempt();
            }
            CorrectionFragment correctionFragment = new CorrectionFragment(goodAnswers);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.question_details_container, qFragment)
                    .replace(R.id.quiz_overview_container, overviewFragment)
                    .replace(R.id.correction_details_container, correctionFragment)
                    .commit();
        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.question_details_container, qFragment)
                    .replace(R.id.quiz_overview_container, overviewFragment)
                    .commit();
        }
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
            case R.id.overview:
                handleToggleOverview();
                break;
            case android.R.id.home:
                if (correction) backHome();
                onBackPressed();
                break;
            case R.id.validate:
                handleValidate(correction);

                break;
        }

        return true;
    }

    private void handleValidate(Boolean correcting) {
        if (correcting) {
            validateDialog = ConfirmDialog.create("Quit quiz results ?", this);
            validateDialog.show(getSupportFragmentManager(), null);
        } else {
            validateDialog =
                    ConfirmDialog.create("Are you sure you want to correct this quiz ?", this);
            validateDialog.show(getSupportFragmentManager(), null);
        }
    }

    private void backHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(USER, user);
        intent.putExtras(bundle);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
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

    // create a list of questions with prefilled fields with values equal to the answers given by
    // the participant
    private List<Question> correctedQuestions() {
        ImmutableList<Question> questions = quiz.getQuestions();
        HashMap<Integer, AnswerModel> answers = model.getAnswers().getValue();
        List<Question> corrected = new ArrayList<Question>();
        for (int i = 0; i < questions.size(); i++) {
            if (questions.get(i).getFormat() instanceof MatrixFormat) {
                MatrixModel answerModel = (MatrixModel) answers.get(i);

                int goodAnswer = questions.get(i).getFormat().correct(answerModel) ? 1 : 0;
                correctedQuestions.add(i, goodAnswer);

                corrected.add(i, makePrefilledMatrixQuestion(answerModel, questions.get(i)));
            }
        }
        return corrected;
    }

    // make a prefilled matrix question
    private Question makePrefilledMatrixQuestion(MatrixModel answerModel, Question quizQuestion) {
        MatrixModel model;
        if (answerModel == null) {
            model = (MatrixModel) quizQuestion.getFormat().getEmptyAnswerModel();
        } else {
            model = answerModel;
        }
        MatrixFormat.Builder builder =
                new MatrixFormat.Builder(model.getNumRows(), model.getNumCols());

        for (int x = 0; x < model.getNumRows(); x++) {
            for (int y = 0; y < model.getNumCols(); y++) {
                builder.withField(x, y, MatrixFormat.Field.preFilledField(model.getAnswer(x, y)));
            }
        }

        return new Question(quizQuestion.getTitle(), quizQuestion.getText(), builder.build());
    }

    @Override
    public void onConfirm(ConfirmDialog dialog) {

        if (correction) {
            backHome();

        } else {
            Quiz questionsLocked = new Quiz("Correction", correctedQuestions());

            Intent intent = new Intent(this, QuizActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Bundle bundle = new Bundle();
            bundle.putSerializable(QUIZ_ID, questionsLocked);
            bundle.putIntegerArrayList(GOOD_ANSWERS, correctedQuestions);
            bundle.putSerializable(USER, user);
            bundle.putBoolean(CORRECTION, true);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }
}
