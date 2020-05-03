package ch.epfl.qedit.view.edit;

import static ch.epfl.qedit.view.edit.EditNewQuizSettingsActivity.STRING_POOL;
import static ch.epfl.qedit.view.edit.EditOverviewFragment.QUESTION;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
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

    private String title;
    private String text;
    private StringPool stringPool;
    private AnswerFormat answerFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_question);

        // Initialize the EditText fields
        setEditTextListener(true);
        setEditTextListener(false);

        // Initialize buttons
        ImageButton numButton = findViewById(R.id.number_button);
        ImageButton textButton = findViewById(R.id.text_button);
        setSolutionButtonListener(numButton, false);
        setSolutionButtonListener(textButton, true);

        setDoneButtonListener();
        setCancelButtonListener();

        // Get the StringPool from the Intent
        Intent intent = getIntent();
        stringPool =
                (StringPool)
                        Objects.requireNonNull(intent.getExtras()).getSerializable(STRING_POOL);
    }

    private void setEditTextListener(final boolean setTitle) {
        int id = setTitle ? R.id.edit_question_title : R.id.edit_question_text;
        EditText editTitle = findViewById(id);
        editTitle.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence s, int start, int count, int after) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (setTitle) title = s.toString();
                        else text = s.toString();
                    }

                    @Override
                    public void afterTextChanged(Editable s) {}
                });
    }

    /** Handles the setup of the two buttons of this activity */
    private void setDoneButtonListener() {
        // Initialize the button that allows to stop editing the question
        Button button = findViewById(R.id.button_done_question_editing);
        button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        returnResultIfNotEmpty();
                    }
                });
    }

    private void setCancelButtonListener() {
        // Initialize the button that allows to stop editing the question
        Button button = findViewById(R.id.button_cancel_question_editing);
        button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setResult(RESULT_CANCELED);
                        finish();
                    }
                });
    }

    private void setSolutionButtonListener(ImageButton button, final boolean text) {
        button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogFragment editFieldFragment = EditFieldFragment.newInstance(text);
                        editFieldFragment.show(getSupportFragmentManager(), SOL_DIALOG_TAG);
                    }
                });
    }

    public void setAnswerFormat(AnswerFormat answerFormat) {
        if (answerFormat != null) {
            TextView helper = findViewById(R.id.choose_answer_text);
            helper.setVisibility(View.GONE);
            this.answerFormat = answerFormat;
        }
    }

    private void returnResultIfNotEmpty() {
        boolean noError = true;
        if (title == null || title.isEmpty()) {
            noError = false;
            EditText titleView = findViewById(R.id.edit_question_title);
            titleView.setError(getString(R.string.cannot_be_empty));
        }
        if (text == null || text.isEmpty()) {
            noError = false;
            EditText textView = findViewById(R.id.edit_question_text);
            textView.setError(getString(R.string.cannot_be_empty));
        }
        if (answerFormat == null) {
            noError = false;
            TextView answerView = findViewById(R.id.choose_answer_text);
            answerView.setError(getString(R.string.cannot_be_empty));
        }
        if (noError) returnResult();
    }

    /** Builds the Question and returns it with the extended StringPool to the callee activity */
    private void returnResult() {
        answerFormat = MatrixFormat.singleField(MatrixFormat.Field.textField("", 25));
        Question question = new Question(stringPool.add(title), stringPool.add(text), answerFormat);
        Intent intent = new Intent();
        intent.putExtra(QUESTION, question);
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
