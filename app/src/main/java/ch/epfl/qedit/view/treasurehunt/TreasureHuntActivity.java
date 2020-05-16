package ch.epfl.qedit.view.treasurehunt;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;
import androidx.lifecycle.ViewModelProvider;

import java.util.Arrays;

import ch.epfl.qedit.R;
import ch.epfl.qedit.model.Question;
import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.model.answer.AnswerFormat;
import ch.epfl.qedit.model.answer.MatrixFormat;
import ch.epfl.qedit.view.quiz.QuestionFragment;
import ch.epfl.qedit.viewmodel.QuizViewModel;

public class TreasureHuntActivity extends AppCompatActivity {

    // We have use the model to communicate the question to the question fragment
    private QuizViewModel model;
    private Quiz quiz;

    // Indicates if that the helper view should be hidden, after the treasure hunt was started
    private boolean hideHelperView;

    // Those are the two alternative views of this activity
    private View helperView;
    private FragmentContainerView questionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treasure_hunt);

        // We find the two alternative views
        helperView = findViewById(R.id.treasure_hunt_helper_view);
        questionView = findViewById(R.id.treasure_hunt_question);
        hideHelperView = false;

        // We retrieve the quiz from the intent that started the activity
//        Intent intent = getIntent();
//        Quiz quiz = (Quiz) Objects.requireNonNull(intent.getExtras()).getSerializable(QUIZ_ID);
        Location loc = new Location("");
        AnswerFormat format = MatrixFormat.singleField(MatrixFormat.Field.textField("", MatrixFormat.Field.NO_LIMIT));
        loc.setLongitude(0);
        loc.setLatitude(0);
        quiz =
                new Quiz(
                        "Title",
                        Arrays.asList(
                                new Question("Q1", "How ?", format, loc, 100),
                                new Question("Q2", "Why ?", format, loc, 100)),
                        true);

        // We retrieve the view model
        model = new ViewModelProvider(this).get(QuizViewModel.class);
        model.setQuiz(quiz);
        model.getFocusedQuestion().setValue(0);
    }

    public void handleTreasureHuntStart(View view) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.treasure_hunt_question, new QuestionFragment())
                .commit();
        helperView.setVisibility(View.GONE);
        questionView.setVisibility(View.VISIBLE);

        hideHelperView = true;
        Intent intent = new Intent(this, QuestionLocatorActivity.class);

        Question firstQuestion = quiz.getQuestions().get(0);
        intent.putExtra(QuestionLocatorActivity.QUESTION_LOCATION, firstQuestion.getLocation());
        intent.putExtra(QuestionLocatorActivity.QUESTION_RADIUS, firstQuestion.getRadius());
        startActivity(intent);
    }
}
