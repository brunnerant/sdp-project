package ch.epfl.qedit.view.edit;

import static ch.epfl.qedit.model.StringPool.TITLE_ID;
import static ch.epfl.qedit.view.home.HomeQuizListFragment.QUIZ_ID;
import static ch.epfl.qedit.view.home.HomeQuizListFragment.STRING_POOL;

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
import java.util.Objects;

public class EditSettingsActivity extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener {
    public static final String QUIZ_BUILDER = "ch.epfl.qedit.model.QUIZ_BUILDER";

    private Quiz.Builder quizBuilder;
    private StringPool stringPool;
    private EditText editTitle;

    // Only used for new quizzes
    private Spinner languageSelectionSpinner;
    private boolean userHasInteracted = false;
    private String languageCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the Quiz and the StringPool from the intent
        Bundle bundle = Objects.requireNonNull(getIntent().getExtras());

        stringPool = (StringPool) bundle.getSerializable(STRING_POOL);
        Quiz quiz = (Quiz) bundle.getSerializable(QUIZ_ID);

        if (quiz != null) {
            setContentView(R.layout.activity_edit_modify_quiz_settings);

            // Initialize the builder with the existing quiz
            quizBuilder = new Quiz.Builder(quiz);

        } else {
            setContentView(R.layout.fragment_edit_quiz_settings);

            // Initialize a new empty QuizBuilder
            quizBuilder = new Quiz.Builder();

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
        }

        // Set the EditText for the title
        editTitle = findViewById(R.id.edit_quiz_title);

        String title = stringPool.get(TITLE_ID);
        // TODO Support old questions that store the strings directly as well
        editTitle.setText((title == null) ? quiz.getTitle() : title);
    }

    public void startEditing(View view) {
        // Update the title in the StringPool and the languageCode
        stringPool.update(TITLE_ID, editTitle.getText().toString());
        stringPool.setLanguageCode(languageCode);

        // Launch the EditQuizActivity
        Intent intent = new Intent(EditSettingsActivity.this, EditQuizActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(QUIZ_BUILDER, quizBuilder);
        bundle.putSerializable(STRING_POOL, stringPool);
        intent.putExtras(bundle);
        startActivity(intent);
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
