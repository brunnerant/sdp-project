package ch.epfl.qedit.view.edit;

import static ch.epfl.qedit.model.StringPool.NO_QUESTION_TEXT_ID;
import static ch.epfl.qedit.model.StringPool.NO_QUESTION_TITLE_ID;
import static ch.epfl.qedit.model.StringPool.TITLE_ID;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import ch.epfl.qedit.R;
import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.model.StringPool;
import ch.epfl.qedit.util.LocaleHelper;
import java.util.Arrays;
import java.util.Locale;

public class EditNewQuizSettingsActivity extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener {
    public static final String QUIZ_BUILDER = "ch.epfl.qedit.model.QUIZ_BUILDER";
    public static final String STRING_POOL = "ch.epfl.qedit.model.STRING_POOL";

    private Quiz.Builder quizBuilder;
    private StringPool stringPool;
    private EditText editTitle;
    private Spinner languageSelectionSpinner;
    private boolean userHasInteracted = false;
    private String languageCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_new_quiz_settings);

        // Initialize a new QuizBuilder and new StringPool
        quizBuilder = new Quiz.Builder();
        stringPool = new StringPool();

        // Create spinner (language list)
        languageSelectionSpinner = findViewById(R.id.edit_language_selection);

        // Find app's current language position in languages list
        String currentLanguage = Locale.getDefault().getLanguage();
        String[] languageList = getResources().getStringArray(R.array.languages_codes);
        int positionInLanguageList = Arrays.asList(languageList).indexOf(currentLanguage);

        // Set current language in spinner at startup
        languageSelectionSpinner.setSelection(positionInLanguageList, false);
        // Set listener
        languageSelectionSpinner.setOnItemSelectedListener(this);

        // Set the EditText for the title
        editTitle = findViewById(R.id.edit_new_quiz_title);
    }

    public void startEditing(View view) {
        // Add the title and two strings for new empty questions to the StringPool
        stringPool.update(TITLE_ID, editTitle.getText().toString());
        stringPool.update(
                NO_QUESTION_TITLE_ID, getResources().getString(R.string.no_question_title_message));
        stringPool.update(
                NO_QUESTION_TEXT_ID, getResources().getString(R.string.no_question_text_message));

        // Launch the EditQuizActivity
        Intent intent = new Intent(EditNewQuizSettingsActivity.this, EditQuizActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(QUIZ_BUILDER, quizBuilder);
        bundle.putSerializable(STRING_POOL, stringPool);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    /* This method tells us if the user has interacted with the activity since it was started */
    public void onUserInteraction() {
        super.onUserInteraction();
        userHasInteracted = true;
    }

    @Override
    /* This method runs if the user selects another language */
    public void onItemSelected(AdapterView parent, View view, int pos, long id) {
        // Do not run if user has not chosen a language
        if (!userHasInteracted) {
            return;
        }

        // Get language code from the position of the clicked language in the spinner
        languageCode = getResources().getStringArray(R.array.languages_codes)[pos];
    }

    @Override
    public void onNothingSelected(AdapterView parent) {
        // Not used because there will always be something selected
    }

    @Override
    /* This method is needed to apply the desired language at the activity startup */
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }
}
