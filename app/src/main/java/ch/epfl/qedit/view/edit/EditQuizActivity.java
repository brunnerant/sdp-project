package ch.epfl.qedit.view.edit;

import static ch.epfl.qedit.view.edit.EditNewQuizSettingsActivity.QUIZ_BUILDER;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import ch.epfl.qedit.R;
import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.viewmodel.EditionViewModel;
import java.util.Objects;

public class EditQuizActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        Intent intent = getIntent();
        Quiz.Builder quizBuilder =
                (Quiz.Builder)
                        Objects.requireNonNull(intent.getExtras()).getSerializable(QUIZ_BUILDER);
        //        StringPool stringPool =
        //                (StringPool)
        //
        // Objects.requireNonNull(intent.getExtras()).getSerializable(STRING_POOL);
        //
        //        // Testing, remove TODO
        //        Quiz quiz =
        //                new Quiz(
        //                        "Test",
        //                        Arrays.asList(
        //                                new Question(
        //                                        "The matches problem",
        //                                        "How many matches can fit in a shoe of size 43?",
        //                                        "matrix3x3"),
        //                                new Question(
        //                                        "Pigeons",
        //                                        "How many pigeons are there on Earth? (Hint: do
        // not count yourself)",
        //                                        "matrix1x1"),
        //                                new Question("KitchenBu", "Oyster", "matrix1x1"),
        //                                new Question(
        //                                        "Everything",
        //                                        "What is the answer to life the universe and
        // everything?",
        //                                        "matrix3x3"),
        //                                new Question(
        //                                        "Banane", "Combien y a-t-il de bananes ?",
        // "matrix1x1")));
        //
        //        quizBuilder.add(quiz.getQuestions().get(0));
        //        quizBuilder.add(quiz.getQuestions().get(1));
        //        quizBuilder.add(quiz.getQuestions().get(2));
        //        quizBuilder.add(quiz.getQuestions().get(3));

        EditionViewModel model = new ViewModelProvider(this).get(EditionViewModel.class);
        model.initializeOverviewList(quizBuilder.getQuestions());

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.quiz_overview_container, new EditOverviewFragment())
                .replace(R.id.question_details_container, new EditQuestionFragment())
                .commit();
    }
}
