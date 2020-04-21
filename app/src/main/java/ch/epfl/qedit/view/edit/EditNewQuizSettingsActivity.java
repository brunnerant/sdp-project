package ch.epfl.qedit.view.edit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import ch.epfl.qedit.R;
import ch.epfl.qedit.model.Question;
import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.viewmodel.EditionViewModel;
import java.util.Arrays;

public class EditNewQuizSettingsActivity extends AppCompatActivity {
    private EditionViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_new_quiz_settings);

        model = new ViewModelProvider(this).get(EditionViewModel.class);

        // Testing, remove TODO
        Quiz quiz =
                new Quiz(
                        "Test",
                        Arrays.asList(
                                new Question(
                                        "The matches problem",
                                        "How many matches can fit in a shoe of size 43?",
                                        "matrix3x3"),
                                new Question(
                                        "Pigeons",
                                        "How many pigeons are there on Earth? (Hint: do not count yourself)",
                                        "matrix1x1"),
                                new Question("KitchenBu", "Oyster", "matrix1x1"),
                                new Question(
                                        "Everything",
                                        "What is the answer to life the universe and everything?",
                                        "matrix3x3"),
                                new Question(
                                        "Banane", "Combien y a-t-il de bananes ?", "matrix1x1")));
        model.getQuizBuilder().add(quiz.getQuestions().get(0));
        model.getQuizBuilder().add(quiz.getQuestions().get(1));
        model.getQuizBuilder().add(quiz.getQuestions().get(2));
        model.getQuizBuilder().add(quiz.getQuestions().get(3));
        model.setQuizTitle(quiz.getTitle());
    }

    public void startEditing(View view) {
        Intent intent = new Intent(EditNewQuizSettingsActivity.this, EditQuizActivity.class);
        startActivity(intent);
    }
}
