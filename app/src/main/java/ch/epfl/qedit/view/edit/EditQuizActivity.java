package ch.epfl.qedit.view.edit;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static ch.epfl.qedit.view.edit.EditQuizSettingsDialog.QUIZ_BUILDER;
import static ch.epfl.qedit.view.home.HomeQuizListFragment.QUIZ_ID;
import static ch.epfl.qedit.view.home.HomeQuizListFragment.STRING_POOL;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import ch.epfl.qedit.R;
import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.model.StringPool;
import ch.epfl.qedit.util.LocaleHelper;
import ch.epfl.qedit.view.util.ConfirmDialog;
import ch.epfl.qedit.view.util.InfoDialog;
import ch.epfl.qedit.viewmodel.EditionViewModel;
import java.util.Objects;

/**
 * This class prepares the ViewModel and launches the Overview and the Preview fragment used for
 * editing quizzes
 */
public class EditQuizActivity extends AppCompatActivity
        implements ConfirmDialog.ConfirmationListener {

    private EditionViewModel model;
    private Quiz.Builder quizBuilder;
    private Boolean overviewActive;

    private ConfirmDialog exitDialog;
    private ConfirmDialog doneDialog;
    private InfoDialog cantSaveEmptyQuizDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // Get the QuizBuilder and the StringPool from the intent
        Intent intent = getIntent();
        quizBuilder =
                (Quiz.Builder)
                        Objects.requireNonNull(intent.getExtras()).getSerializable(QUIZ_BUILDER);
        StringPool stringPool =
                (StringPool)
                        Objects.requireNonNull(intent.getExtras()).getSerializable(STRING_POOL);
        // Show the overview
        overviewActive = true;

        // Configure ConfirmationDialogs
        setupDialogs();

        // Hide up navigation
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);

        // Initialize the ViewModel
        model = new ViewModelProvider(this).get(EditionViewModel.class);
        model.setQuizBuilder(quizBuilder);
        model.setStringPool(stringPool);

        // Launch the the two fragments in this activity
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.quiz_overview_container, new EditOverviewFragment())
                .replace(R.id.question_details_container, new EditPreviewFragment())
                .commit();
    }

    /** This method configures the two Confirmation Dialogs that we use in this activity */
    private void setupDialogs() {
        exitDialog = ConfirmDialog.create(getString(R.string.warning_exit_edition), this);
        doneDialog = ConfirmDialog.create(getString(R.string.warning_done_edition), this);
        cantSaveEmptyQuizDialog = InfoDialog.create(getString(R.string.warning_save_empty_quiz));
    }

    @Override
    /* This method is needed to apply the desired language at the activity startup */
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.next:
            case R.id.previous:
                handleNavigation(id == R.id.next ? 1 : -1);
                break;
            case R.id.overview:
                handleToggleOverview();
                break;
            case R.id.exit:
                exitDialog.show(getSupportFragmentManager(), "exit_confirmation");
                break;
            case R.id.done:
                if (quizBuilder.size() == 0) {
                    cantSaveEmptyQuizDialog.show(
                            getSupportFragmentManager(), "save_empty_quiz_information");
                } else {
                    doneDialog.show(getSupportFragmentManager(), "done_confirmation");
                }
                break;
        }

        return true;
    }

    /** This function handles navigating back and forth between questions */
    private void handleNavigation(int offset) {
        MutableLiveData<Integer> focusedQuestion = model.getFocusedQuestion();
        Integer index = focusedQuestion.getValue();

        if (index == null) {
            focusedQuestion.setValue(0);
        } else if ((index + offset) < quizBuilder.size() && (index + offset) >= 0) {
            focusedQuestion.setValue(index + offset);
        }
    }

    /** This function handles toggling the overview fragment */
    private void handleToggleOverview() {
        if (overviewActive) {
            findViewById(R.id.quiz_overview_container).setVisibility(GONE);
            findViewById(R.id.separator).setVisibility(GONE);
        } else {
            findViewById(R.id.quiz_overview_container).setVisibility(VISIBLE);
            findViewById(R.id.separator).setVisibility(VISIBLE);
        }
        overviewActive = !overviewActive;
    }

    /** This method builds the edited quiz and returns it along with the string pool */
    private void returnEditedResult() {
        Intent intent = new Intent();
        intent.putExtra(QUIZ_ID, model.getQuizBuilder().build());
        intent.putExtra(STRING_POOL, model.getStringPool());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onConfirm(ConfirmDialog dialog) {
        if (dialog == exitDialog) {
            // Return that the edition has been canceled, no need to do anything
            setResult(RESULT_CANCELED);
            finish();
        } else { // Then it must be the doneDialog
            returnEditedResult();
        }
    }
}
