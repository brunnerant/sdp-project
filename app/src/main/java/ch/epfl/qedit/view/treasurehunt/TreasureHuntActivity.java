package ch.epfl.qedit.view.treasurehunt;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.util.Objects;

import ch.epfl.qedit.R;
import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.viewmodel.QuizViewModel;

import static ch.epfl.qedit.view.home.HomeQuizListFragment.QUIZ_ID;

public class TreasureHuntActivity extends AppCompatActivity {

    // We have use the model to communicate the question to the question fragment
    private QuizViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treasure_hunt);

        // We retrieve the quiz from the intent that started the activity
        Intent intent = getIntent();
        Quiz quiz = (Quiz) Objects.requireNonNull(intent.getExtras()).getSerializable(QUIZ_ID);

        // We retrieve the view model
        model = new ViewModelProvider(this).get(QuizViewModel.class);
        model.setQuiz(quiz);
        model.getFocusedQuestion().setValue(0);
    }
}
