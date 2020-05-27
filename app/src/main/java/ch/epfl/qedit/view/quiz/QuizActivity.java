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
import ch.epfl.qedit.model.answer.MatrixFormat;
import ch.epfl.qedit.model.answer.MatrixModel;
import ch.epfl.qedit.util.LocaleHelper;
import ch.epfl.qedit.view.util.ConfirmDialog;
import ch.epfl.qedit.viewmodel.QuizViewModel;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class QuizActivity extends AppCompatActivity implements ConfirmDialog.ConfirmationListener {
    public static final String PARTICIPANT_ANSWERS = "ch.epfl.qedit.quiz.PARTICIPANT_ANSWERS";
    public static final String QUESTIONS = "ch.epfl.qedit.quiz.QUESTIONS";
    public static final String GOOD_ANSWERS = "ch.epfl.qedit.quiz.GOOD_ANSWERS";
    private QuizViewModel model;
    private Boolean overviewActive;

    private ConfirmDialog validateDialog;
    private Quiz quiz;

    private ArrayList<Integer> correctedQuestions;
    private ArrayList<Integer> goodAnswers = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // Get the Intent that started this activity and extract the quiz
        Intent intent = getIntent();
        quiz = (Quiz) Objects.requireNonNull(intent.getExtras()).getSerializable(QUIZ_ID);

        goodAnswers = (ArrayList<Integer>) intent.getExtras().getSerializable(GOOD_ANSWERS);
        model = new ViewModelProvider(this).get(QuizViewModel.class);
        model.setQuiz(quiz);
        correctedQuestions = new ArrayList<>();
        overviewActive = false;
        handleToggleOverview();

        validateDialog = ConfirmDialog.create("Are you sure you want to correct this quiz ?", this);

        QuestionFragment questionFragment = new QuestionFragment();
        QuizOverviewFragment overview = new QuizOverviewFragment();

        Bundle bundle = new Bundle();
        bundle.putIntegerArrayList(GOOD_ANSWERS, goodAnswers);
        questionFragment.setArguments(bundle);

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

    // create a list of questions with prefilled fields with values equal to the answers given by
    // the participant
    private List<Question> correctedQuestions() {
        ImmutableList<Question> questions = quiz.getQuestions();
        HashMap<Integer, AnswerModel> answers = model.getAnswers().getValue();
        List<Question> corrected = new ArrayList<Question>();
        for (int i = 0; i < questions.size(); i++) {
            if (questions.get(i).getFormat() instanceof MatrixFormat) {
                MatrixModel answerModel = (MatrixModel) answers.get(i);

                Integer goodAnswer = questions.get(i).getFormat().correct(answerModel) ? 1 : 0;
                correctedQuestions.add(i, goodAnswer);

                corrected.add(i, makePrefilledMatrixQuestion(answerModel, questions.get(i)));
            }
        }
        return corrected;
    }
    // make a prefilled matrix question
    private Question makePrefilledMatrixQuestion(MatrixModel answerModel, Question quizQuestion) {
        MatrixFormat.Builder builder =
                new MatrixFormat.Builder(answerModel.getNumRows(), answerModel.getNumCols());

        for (int x = 0; x < answerModel.getNumRows(); x++) {
            for (int y = 0; y < answerModel.getNumCols(); y++) {
                builder.withField(
                        x, y, MatrixFormat.Field.preFilledField(answerModel.getAnswer(x, y)));
            }
        }

        return new Question(quizQuestion.getTitle(), quizQuestion.getText(), builder.build());
    }

    @Override
    public void onConfirm(ConfirmDialog dialog) {
        /* ImmutableList<Question> questions = quiz.getQuestions();
        HashMap<Integer, AnswerModel> answers = model.getAnswers().getValue();
        Bundle bundle = new Bundle();
        bundle.putSerializable(PARTICIPANT_ANSWERS,answers);
        bundle.putSerializable(QUESTIONS,questions);

        Intent intent = new Intent(this,QuizResultActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);*/
        Quiz questionsLocked = new Quiz("Correction", correctedQuestions());
        Intent intent = new Intent(this, QuizActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(QUIZ_ID, questionsLocked);
        bundle.putIntegerArrayList(GOOD_ANSWERS, correctedQuestions);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
