package ch.epfl.qedit.view.edit;

import static ch.epfl.qedit.model.StringPool.NO_QUESTION_TEXT_ID;
import static ch.epfl.qedit.model.StringPool.NO_QUESTION_TITLE_ID;
import static ch.epfl.qedit.model.StringPool.TITLE_ID;
import static ch.epfl.qedit.view.edit.EditNewQuizSettingsActivity.QUIZ_BUILDER;
import static ch.epfl.qedit.view.edit.EditNewQuizSettingsActivity.STRING_POOL;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import ch.epfl.qedit.R;
import ch.epfl.qedit.model.Question;
import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.model.StringPool;
import java.util.Arrays;

public class EditModifyQuizSettingsActivity extends AppCompatActivity {
    private Quiz.Builder quizBuilder;
    private StringPool stringPool;
    private EditText editTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_modify_quiz_settings);

        // Initialize a new QuizBuilder and new StringPool TODO get from bundle
        stringPool = new StringPool();
        quizBuilder = new Quiz.Builder(createTestQuiz());

        // Set the EditText for the title
        editTitle = findViewById(R.id.edit_modify_quiz_title);
        editTitle.setText(stringPool.get(TITLE_ID));
    }

    public void startEditing(View view) {
        // Update the title in the StringPool
        stringPool.update(TITLE_ID, editTitle.getText().toString());

        // Launch the EditQuizActivity
        Intent intent = new Intent(EditModifyQuizSettingsActivity.this, EditQuizActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(QUIZ_BUILDER, quizBuilder);
        bundle.putSerializable(STRING_POOL, stringPool);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /** Remove in next sprint */
    private Quiz createTestQuiz() {
        // TODO remove, only for testing
        Quiz quiz =
                new Quiz(
                        "Test Title",
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

        stringPool.update(TITLE_ID, quiz.getTitle());
        stringPool.update(
                NO_QUESTION_TITLE_ID, getResources().getString(R.string.no_question_title_message));
        stringPool.update(
                NO_QUESTION_TEXT_ID, getResources().getString(R.string.no_question_text_message));

        return quiz;
    }
}
