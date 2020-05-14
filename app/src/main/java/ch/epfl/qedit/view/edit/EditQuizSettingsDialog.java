package ch.epfl.qedit.view.edit;

import static android.view.View.GONE;
import static ch.epfl.qedit.model.StringPool.TITLE_ID;
import static ch.epfl.qedit.view.home.HomeQuizListFragment.QUIZ_ID;
import static ch.epfl.qedit.view.home.HomeQuizListFragment.STRING_POOL;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import ch.epfl.qedit.R;
import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.model.StringPool;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

public class EditQuizSettingsDialog extends DialogFragment
        implements AdapterView.OnItemSelectedListener {

    /** This interface is used to be notified when the user entered the text. */
    public interface SubmissionListener extends Serializable {
        void onSubmit(StringPool stringPool, Quiz.Builder quizBuilder);
    }

    /**
     * This interface is used to filter the allowed text inputs. To allow a string in the dialog, it
     * should return null. To indicate an error, it simply returns the string containing the error
     * message, which will be displayed next to the edit text.
     */
    public interface TextFilter {
        String isAllowed(String text);
    }

    public static final TextFilter NO_FILTER = text -> null;

    public static final String QUIZ_BUILDER = "ch.epfl.qedit.model.QUIZ_BUILDER";
    private static final String LISTENER_KEY = "listener";

    private Quiz.Builder quizBuilder;
    private boolean editExistingQuiz;
    private StringPool stringPool;
    private Quiz quiz;

    private EditText editTitle;
    private TextFilter textFilter = NO_FILTER;
    private SubmissionListener listener;

    private boolean hasTreasureHuntCheckBox;

    // Only used for new quizzes
    private String languageCode;

    public static EditQuizSettingsDialog newInstance(SubmissionListener listener) {
        return newInstance(listener, null, null);
    }

    public static EditQuizSettingsDialog newInstance(
            SubmissionListener listener, StringPool stringPool, Quiz quiz) {
        EditQuizSettingsDialog dialog = new EditQuizSettingsDialog();

        Bundle args = new Bundle();
        args.putSerializable(LISTENER_KEY, listener);

        if (stringPool == null) {
            stringPool = new StringPool();
        }
        args.putSerializable(STRING_POOL, stringPool);

        if (quiz != null) {
            args.putSerializable(QUIZ_ID, quiz);
        }

        dialog.setArguments(args);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getArgumentsAndSetView();

        // Set the EditText for the title
        editTitle = view.findViewById(R.id.edit_quiz_title);
        editTitle.setInputType(InputType.TYPE_CLASS_TEXT);

        if (quiz != null) {
            setupModifyingExistingQuiz(quiz, view);
        } else {
            setupLanguageSpinner(view);

            // Set treasure hunt
            createTreasureHuntCheckbox(view);
        }

        Spanned title =
                Html.fromHtml(
                        "<font color='#FF0000'>"
                                + getString(R.string.edit_dialog_title_settings)
                                + "</font>");

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        builder.setView(view)
                .setTitle(title)
                .setCancelable(true)
                .setPositiveButton(
                        R.string.button_start_editing_message, (dialog, id) -> prepareEditing())
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();

        setupEditTitleTextWatcher(dialog);

        // Create the AlertDialog object and return it
        return dialog;
    }

    public void setTextFilter(TextFilter textFilter) {
        this.textFilter = Objects.requireNonNull(textFilter);
    }

    @Override
    /* This method runs if the user selects another language */
    public void onItemSelected(AdapterView parent, View view, int pos, long id) {
        // Get language code from the position of the clicked language in the spinner
        languageCode = getResources().getStringArray(R.array.languages_codes)[pos];
    }

    @Override
    public void onNothingSelected(AdapterView parent) {
        // Not used because there will always be something selected
    }

    private View getArgumentsAndSetView() {
        listener = (SubmissionListener) getArguments().getSerializable(LISTENER_KEY);
        stringPool = (StringPool) getArguments().getSerializable(STRING_POOL);
        quiz = (Quiz) getArguments().getSerializable(QUIZ_ID);

        LayoutInflater inflater = Objects.requireNonNull(requireActivity()).getLayoutInflater();
        return inflater.inflate(R.layout.fragment_edit_quiz_settings, null);
    }

    private void setupModifyingExistingQuiz(Quiz quiz, View view) {
        editExistingQuiz = true;

        // Initialize the builder with the existing quiz
        quizBuilder = new Quiz.Builder(quiz);

        String title = stringPool.get(TITLE_ID);
        // TODO Support old questions that store the strings directly as well
        editTitle.setText((title == null) ? quiz.getTitle() : title);
        editTitle.setSelection(editTitle.getText().length());

        hasTreasureHuntCheckBox = false;
        view.findViewById(R.id.edit_language_selection).setVisibility(GONE);
        view.findViewById(R.id.treasure_hunt_checkbox).setVisibility(GONE);
    }

    private void setupLanguageSpinner(View view) {
        // Create spinner (language list)
        Spinner languageSelectionSpinner = view.findViewById(R.id.edit_language_selection);

        // Find app's current language position in languages list
        String currentLanguage = Locale.getDefault().getLanguage();
        String[] languageList = getResources().getStringArray(R.array.languages_codes);
        int positionInLanguageList = Arrays.asList(languageList).indexOf(currentLanguage);

        // Set current language in spinner at startup
        languageSelectionSpinner.setSelection(positionInLanguageList, false);

        // Set listener
        languageSelectionSpinner.setOnItemSelectedListener(this);
    }

    private void createTreasureHuntCheckbox(View view) {
        CheckBox treasureHuntCheckbox = view.findViewById(R.id.treasure_hunt_checkbox);
        treasureHuntCheckbox.setOnClickListener(
                v -> hasTreasureHuntCheckBox = ((CheckBox) v).isChecked());
    }

    private void setupEditTitleTextWatcher(AlertDialog dialog) {
        editTitle.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(
                            CharSequence s, int start, int count, int after) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        String error = textFilter.isAllowed(s.toString());
                        Button positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);

                        if (error != null) {
                            editTitle.setError(error);
                            positive.setEnabled(false);
                        } else {
                            positive.setEnabled(true);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {}
                });

        if (!editExistingQuiz) {
            dialog.setOnShowListener(dialog1 -> editTitle.setText(""));
        }
    }

    private void prepareEditing() {
        // Update the title in the StringPool and the languageCode
        stringPool.update(TITLE_ID, editTitle.getText().toString());
        stringPool.setLanguageCode(languageCode);

        if (!editExistingQuiz) {
            // Initialize a new empty QuizBuilder
            quizBuilder = new Quiz.Builder(hasTreasureHuntCheckBox);
        }

        listener.onSubmit(stringPool, quizBuilder);
    }
}
