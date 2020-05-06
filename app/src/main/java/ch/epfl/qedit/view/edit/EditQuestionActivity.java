package ch.epfl.qedit.view.edit;

import static ch.epfl.qedit.view.edit.EditNewQuizSettingsActivity.STRING_POOL;
import static ch.epfl.qedit.view.edit.EditOverviewFragment.QUESTION;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import ch.epfl.qedit.util.LocaleHelper;
import java.util.Objects;

public class EditQuestionActivity extends AppCompatActivity {

    public static final String SOL_DIALOG_TAG = "ch.epfl.qedit.view.edit.EDIT_SOL_DIALOG_TAG";

    private String titleId;
    private String textId;
    private StringPool stringPool;
    private AnswerFormat answerFormat;

    private EditText titleView;
    private EditText textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_question);

        // Initialize the EditText fields
        titleView = findViewById(R.id.edit_question_title);
        textView = findViewById(R.id.edit_question_text);
        titleView.addTextChangedListener(editTextListener(true));
        textView.addTextChangedListener(editTextListener(false));

        // Initialize buttons
        ImageButton numButton = findViewById(R.id.number_button);
        ImageButton textButton = findViewById(R.id.text_button);
        setSolutionButtonListener(numButton, false);
        setSolutionButtonListener(textButton, true);

        setDoneButtonListener();
        setCancelButtonListener();

        // Get the StringPool, the title and the text from the Intent
        extractFromIntent();
    }

    /** Extract string pool and question attributes to modify */
    private void extractFromIntent() {
        Bundle bundle = Objects.requireNonNull(getIntent().getExtras());
        stringPool = (StringPool) bundle.getSerializable(STRING_POOL);
        Question question = (Question) bundle.getSerializable(QUESTION);

        // If we are modifying a question, pre-set title, text and answerFormat
        if (question != null) {
            titleId = question.getTitle();
            textId = question.getText();
            titleView.setText(stringPool.get(titleId));
            textView.setText(stringPool.get(textId));
            setAnswerFormat(question.getFormat());
        }
    }

    /**
     * Create a listener for either the title view or the text view (factorise)
     *
     * @param title select if we set listener for title or text
     */
    private TextWatcher editTextListener(final boolean title) {
        return new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (title) updateTitle(s.toString());
                else updateText(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };
    }

    private void updateTitle(String str) {
        if (titleId == null) titleId = stringPool.add(str);
        else stringPool.update(titleId, str);
    }

    private void updateText(String str) {
        if (textId == null) textId = stringPool.add(str);
        else stringPool.update(textId, str);
    }

    /** Handles the setup of the two buttons of this activity */
    private void setDoneButtonListener() {
        // Initialize the button that allows to stop editing the question
        Button button = findViewById(R.id.button_done_question_editing);
        button.setOnClickListener(v -> returnResult());
    }

    private void setCancelButtonListener() {
        // Initialize the button that allows to stop editing the question
        Button button = findViewById(R.id.button_cancel_question_editing);
        button.setOnClickListener(
                v -> {
                    setResult(RESULT_CANCELED);
                    finish();
                });
    }

    /** Handles the setup of choose answer type button */
    private void setSolutionButtonListener(ImageButton button, final boolean text) {
        button.setOnClickListener(
                v -> {
                    DialogFragment editFieldFragment = EditFieldFragment.newInstance(text);
                    editFieldFragment.show(getSupportFragmentManager(), SOL_DIALOG_TAG);
                });
    }

    /** This function is called in the open dialog when we click on a solution button */
    public void setAnswerFormat(AnswerFormat answerFormat) {
        if (answerFormat != null) {
            TextView helper = findViewById(R.id.choose_answer_text);
            helper.setText(R.string.correct_answer_specified);
            helper.setError(null);
            this.answerFormat = answerFormat;
        }
    }

    /**
     * Builds the Question and returns it with the extended StringPool to the callee activity.
     * Return only if title and text are non-empty and answer format is not null.
     */
    private void returnResult() {
        // test if title, text and answerFormat are non-empty
        // use & operator because we want to evaluate both side
        boolean noError = setErrorIfEmpty(titleId, R.id.edit_question_title);
        noError &= setErrorIfEmpty(textId, R.id.edit_question_text);
        if (answerFormat == null) {
            noError = false;
            TextView answerView = findViewById(R.id.choose_answer_text);
            answerView.setError(getString(R.string.cannot_be_empty));
        }
        if (noError) {
            // return the Question created by this activity to the callee activity
            Question question = new Question(titleId, textId, answerFormat);
            Intent intent = new Intent();
            intent.putExtra(QUESTION, question);
            intent.putExtra(STRING_POOL, stringPool);
            setResult(RESULT_OK, intent);
            finish();
        }
        ;
    }

    /**
     * Set an error to the view of a string if it is empty
     *
     * @param str string on which we test if its empty
     * @param strViewId View attached to the object tested
     * @return true if there is no error set, false otherwise
     */
    private boolean setErrorIfEmpty(String str, int strViewId) {
        if (str == null || stringPool.get(str) == null || stringPool.get(str).isEmpty()) {
            EditText strView = findViewById(strViewId);
            strView.setError(getString(R.string.cannot_be_empty));
            return false;
        }
        return true;
    }

    @Override
    /* This method is needed to apply the desired language at the activity startup */
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }
}
