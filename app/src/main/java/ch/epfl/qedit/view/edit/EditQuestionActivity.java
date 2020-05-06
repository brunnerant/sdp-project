package ch.epfl.qedit.view.edit;

import static ch.epfl.qedit.view.edit.EditOverviewFragment.QUESTION;
import static ch.epfl.qedit.view.edit.EditSettingsActivity.STRING_POOL;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import ch.epfl.qedit.R;
import ch.epfl.qedit.model.Question;
import ch.epfl.qedit.model.StringPool;
import ch.epfl.qedit.model.answer.AnswerFormat;
import ch.epfl.qedit.model.answer.MatrixFormat;
import ch.epfl.qedit.util.LocaleHelper;
import java.util.Objects;

public class EditQuestionActivity extends AppCompatActivity {
    private String title;
    private String text;

    private EditText editTitle;
    private EditText editText;
    private StringPool stringPool;
    private AnswerFormat answerFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_question);

        // Initialize the EditText fields
        editTitle = findViewById(R.id.edit_question_title);
        editText = findViewById(R.id.edit_question_text);

        // Initialize buttons
        initializeButtons();

        // Get the StringPool from the Intent
        Intent intent = getIntent();
        stringPool =
                (StringPool)
                        Objects.requireNonNull(intent.getExtras()).getSerializable(STRING_POOL);
    }

    /** Handles the setup of the two buttons of this activity */
    private void initializeButtons() {
        // Initialize the button that allows to add an Answer
        ImageButton addButton = findViewById(R.id.add_button);
        addButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        switchToEditAnswerActivity();
                    }
                });

        // Initialize the button that allows to stop editing the question
        Button doneButton = findViewById(R.id.button_done_editing);
        doneButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        // Setup dummy result (temporarily)
                        setupDummyResult();
                        returnResult();
                    }
                });
    }

    /** Remove when the real question is ready */
    private void setupDummyResult() {
        title = stringPool.add("This is a new title");
        text = stringPool.add("This is a new text");
        answerFormat = MatrixFormat.singleField(MatrixFormat.Field.textField("", 25));
    }

    /** Builds the Question and returns it with the extended StringPool to the callee activity */
    private void returnResult() {
        Intent intent = new Intent();
        intent.putExtra(QUESTION, new Question(title, text, answerFormat));
        intent.putExtra(STRING_POOL, stringPool);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void switchToEditAnswerActivity() {
        Intent intent = new Intent(EditQuestionActivity.this, EditAnswerActivity.class);
        startActivity(intent);
    }

    @Override
    /* This method is needed to apply the desired language at the activity startup */
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }
}
