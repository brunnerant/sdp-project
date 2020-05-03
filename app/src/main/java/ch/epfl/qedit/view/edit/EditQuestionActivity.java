package ch.epfl.qedit.view.edit;

import static ch.epfl.qedit.view.edit.EditNewQuizSettingsActivity.STRING_POOL;
import static ch.epfl.qedit.view.edit.EditOverviewFragment.QUESTION;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import ch.epfl.qedit.R;
import ch.epfl.qedit.model.Question;
import ch.epfl.qedit.model.StringPool;
import ch.epfl.qedit.model.answer.AnswerFormat;
import ch.epfl.qedit.model.answer.MatrixFormat;
import ch.epfl.qedit.util.LocaleHelper;
import java.util.Objects;

public class EditQuestionActivity extends AppCompatActivity {

    public static final String SOL_DIALOG_TAG = "ch.epfl.qedit.view.edit.EDIT_SOL_DIALOG_TAG";

    private EditText editTitle;
    private EditText editText;

    private String title;
    private String text;
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
        ImageButton numButton = findViewById(R.id.number_button);
        ImageButton textButton = findViewById(R.id.text_button);
        setSolutionButtonListener(numButton, false);
        setSolutionButtonListener(textButton, true);

        Button doneButton = findViewById(R.id.button_done_question_editing);
        Button cancelButton = findViewById(R.id.button_cancel_question_editing);
        setDoneButtonListener(doneButton);
        setCancelButtonListener(cancelButton);

        // Get the StringPool from the Intent
        Intent intent = getIntent();
        stringPool =
                (StringPool)
                        Objects.requireNonNull(intent.getExtras()).getSerializable(STRING_POOL);
    }

    /** Handles the setup of the two buttons of this activity */
    private void setDoneButtonListener(Button button) {
        // Initialize the button that allows to stop editing the question
        button.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        // Setup dummy result (temporarily) TODO
                        setupDummyResult();
                        returnResult();
                    }
                });
    }

    private void setCancelButtonListener(Button button) {
        // Initialize the button that allows to stop editing the question
        button.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        // Setup dummy result (temporarily) TODO
                        setupDummyResult();
                        returnResult();
                    }
                });
    }

    private void setSolutionButtonListener(ImageButton button, final boolean text) {
        button.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        DialogFragment editFieldFragment = EditFieldFragment.newInstance(text);
                        editFieldFragment.show(getSupportFragmentManager(), SOL_DIALOG_TAG);
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

    @Override
    /* This method is needed to apply the desired language at the activity startup */
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }
}
