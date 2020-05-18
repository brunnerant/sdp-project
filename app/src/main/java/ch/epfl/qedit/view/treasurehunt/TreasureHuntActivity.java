package ch.epfl.qedit.view.treasurehunt;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
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
import ch.epfl.qedit.view.util.ConfirmDialog;
import ch.epfl.qedit.viewmodel.QuizViewModel;

public class TreasureHuntActivity extends AppCompatActivity
        implements ConfirmDialog.ConfirmationListener {

    // We use the model to communicate the question to the question fragment.
    private QuizViewModel model;
    private Quiz quiz;

    // Indicates if that the helper view should be hidden, after the treasure hunt was started.
    private boolean hideHelperView = false;

    // This tells whether the question done item should be shown in the menu. At the start of
    // the activity, it should not be visible.
    private boolean showQuestionDone = false;

    // This is the index of the next question that should be showed when returning from
    // the question locator activity.
    private int nextIndex;

    // Those are the two alternative views of this activity. The first view is used at the start,
    // to help the user, and the second is used to show the question.
    private View helperView;
    private FragmentContainerView questionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treasure_hunt);

        // We find the two alternative views
        helperView = findViewById(R.id.treasure_hunt_helper_view);
        questionView = findViewById(R.id.treasure_hunt_question);

        // We retrieve the quiz from the intent that started the activity
        //        Intent intent = getIntent();
        //        Quiz quiz = (Quiz)
        // Objects.requireNonNull(intent.getExtras()).getSerializable(QUIZ_ID);
        Location loc = new Location("");
        AnswerFormat format =
                MatrixFormat.singleField(
                        MatrixFormat.Field.textField("", MatrixFormat.Field.NO_LIMIT));
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
    }

    @Override
    protected void onResume() {
        super.onResume();

        // If the helper view needs to be hidden (after the treasure hunt started)
        if (hideHelperView) {
            // We add the question fragment
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.treasure_hunt_question, new QuestionFragment())
                    .commit();

            // And swap which view is visible
            helperView.setVisibility(View.GONE);
            questionView.setVisibility(View.VISIBLE);
            hideHelperView = false;

            // We also add the "done" button to the menu, to finish answering the question
            showQuestionDone = true;
            invalidateOptionsMenu(); // tells android to re-draw the options menu
        }

        // Then, we need to communicate the question index to the question fragment
        model.getFocusedQuestion().setValue(nextIndex);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // This function is called when the options menu is created
        getMenuInflater().inflate(R.menu.treasure_hunt_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // This function is called when the options menu is re-drawn
        menu.findItem(R.id.question_done).setVisible(showQuestionDone);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // If the user wants to finish the question, we ask him for confirmation
        if (item.getItemId() == R.id.question_done)
            ConfirmDialog.create(getString(R.string.treasure_hunt_confirm_next_question), this)
                    .show(getSupportFragmentManager(), "confirm_answer");

        return true;
    }

    @Override
    // This is called when the user confirmed that he finished answering the question
    public void onConfirm(ConfirmDialog dialog) {
        int currIndex = model.getFocusedQuestion().getValue();

        if (currIndex == quiz.getQuestions().size() - 1) {
            // If the last question was answered, we finish the activity.
            // In the future, we have to go to the result page.
            finish();
        } else {
            // If there are remaining questions, we locate the next one
            locateQuestion(currIndex + 1);
        }
    }

    // This method is called when clicking on the "start treasure hunt" button (c.f. xml file)
    public void handleTreasureHuntStart(View view) {
        hideHelperView = true;
        locateQuestion(0);
    }

    // Transitions to the question locator view for the given question
    private void locateQuestion(int index) {
        // We save the index, so that when this activity is resumed, we can switch to
        // the next question
        nextIndex = index;
        Question nextQuestion = quiz.getQuestions().get(nextIndex);

        // We give the location and radius as argument to the question locator activity, and start
        // it
        Intent intent = new Intent(this, QuestionLocatorActivity.class);
        Bundle bundle = new Bundle();
        intent.putExtra(QuestionLocatorActivity.QUESTION_LOCATION, nextQuestion.getLocation());
        bundle.putDouble(QuestionLocatorActivity.QUESTION_RADIUS, nextQuestion.getRadius());
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
