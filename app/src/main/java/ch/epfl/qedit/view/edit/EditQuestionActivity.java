package ch.epfl.qedit.view.edit;

import static ch.epfl.qedit.view.edit.EditOverviewFragment.QUESTION;
import static ch.epfl.qedit.view.edit.EditOverviewFragment.TREASURE_HUNT;
import static ch.epfl.qedit.view.home.HomeQuizListFragment.STRING_POOL;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.Nullable;
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
    public static final int MAP_REQUEST_CODE = 1;
    public static final String LATITUDE = "ch.epfl.qedit.view.edit.LATITUDE";
    public static final String LONGITUDE = "ch.epfl.qedit.view.edit.LONGITUDE";

    private String titleId;
    private String textId;
    private StringPool stringPool;
    private AnswerFormat answerFormat;

    private boolean isTreasureHunt;

    private EditText titleView;
    private EditText textView;
    private Button button_choose_location;

    private double longitude;
    private double latitude;
    private double radius;
    private boolean hasBeenSet = false;

    private TextView longitudeText;
    private TextView latitudeText;
    private EditText radiusText;

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

        // Get the StringPool, the title and the text and treasure hunt from the Intent
        extractFromIntent();

        setTreasureHunt();
    }

    private void setTreasureHunt() {
        longitudeText = findViewById(R.id.longitude_text);
        latitudeText = findViewById(R.id.latitude_text);
        radiusText = findViewById(R.id.radius_text);
        button_choose_location = findViewById(R.id.edit_choose_location);

        if (isTreasureHunt) {
            // TODO: Do something... maybe
            int type = InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL;
            radiusText.setInputType(type);
            setChooseLocation();
        } else {
            latitudeText.setVisibility(View.INVISIBLE);
            longitudeText.setVisibility(View.INVISIBLE);
            radiusText.setVisibility(View.INVISIBLE);
            button_choose_location.setVisibility(View.INVISIBLE);
        }
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

        // TODO: Rename treasure hunt is needed
        isTreasureHunt = (boolean) bundle.getSerializable(TREASURE_HUNT);
    }

    private void setChooseLocation() {
        button_choose_location.setOnClickListener(v -> launchMapActivity());
    }

    private void launchMapActivity() {
        Intent mapActivityIntent = new Intent(this, EditMapsActivity.class);
        startActivityForResult(mapActivityIntent, MAP_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MAP_REQUEST_CODE) {
            longitude = (double) data.getExtras().getSerializable(LONGITUDE);
            latitude = (double) data.getExtras().getSerializable(LATITUDE);
            hasBeenSet = true;

            longitudeText.setText(Double.toString(longitude));
            longitudeText.setError(null);

            latitudeText.setText(Double.toString(latitude));
            latitudeText.setError(null);
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
                    DialogFragment editFieldFragment = EditFieldFragment.newInstance(text, false);
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

        noError &= errorTreasureHunt(noError);

        if (answerFormat == null) {
            noError = false;
            TextView answerView = findViewById(R.id.choose_answer_text);
            answerView.setError(getString(R.string.cannot_be_empty));
        }
        if (noError) {
            // return the Question created by this activity to the callee activity
            Question question = getQuestionForReturnResult();

            Intent intent = new Intent();
            intent.putExtra(QUESTION, question);
            intent.putExtra(STRING_POOL, stringPool);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    private boolean errorTreasureHunt(boolean noError) {
        if (isTreasureHunt) {
            noError &= setErrorIfEmpty(radiusText.getText().toString(), R.id.radius_text);

            System.out.println(
                    "Strangeeee eioa eioe aoien ioevn oiv no novin eoi vo " + hasBeenSet);
            noError &= setErrorTextView(R.id.longitude_text, hasBeenSet);
            noError &= setErrorTextView(R.id.latitude_text, hasBeenSet);
        }

        return noError;
    }

    private Question getQuestionForReturnResult() {
        Question question;
        if (isTreasureHunt) {
            radius = Double.parseDouble(radiusText.getText().toString());
            question = new Question(titleId, textId, answerFormat, longitude, latitude, radius);
        } else {
            question = new Question(titleId, textId, answerFormat);
        }

        return question;
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

    private boolean setErrorTextView(int strViewId, boolean b) {
        if (!b) {
            TextView strView = findViewById(strViewId);
            strView.setError(getString(R.string.cannot_be_empty));
            return false;
        } else {
            TextView strView = findViewById(strViewId);
            strView.setError(null);
        }
        return true;
    }

    @Override
    /* This method is needed to apply the desired language at the activity startup */
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }
}
