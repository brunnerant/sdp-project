package ch.epfl.qedit.view.edit;

import static ch.epfl.qedit.view.edit.EditNewQuizSettingsActivity.QUIZ_BUILDER;
import static ch.epfl.qedit.view.edit.EditNewQuizSettingsActivity.STRING_POOL;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import ch.epfl.qedit.R;
import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.model.StringPool;
import ch.epfl.qedit.util.LocaleHelper;
import ch.epfl.qedit.viewmodel.EditionViewModel;
import java.util.Objects;

/**
 * This class prepares the ViewModel and launches the Overview and the Preview fragment used for
 * editing quizzes
 */
public class EditQuizActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // Get the QuizBuilder and the StringPool from the intent
        Intent intent = getIntent();
        Quiz.Builder quizBuilder =
                (Quiz.Builder)
                        Objects.requireNonNull(intent.getExtras()).getSerializable(QUIZ_BUILDER);
        StringPool stringPool =
                (StringPool)
                        Objects.requireNonNull(intent.getExtras()).getSerializable(STRING_POOL);

        // Initialize the ViewModel
        EditionViewModel model = new ViewModelProvider(this).get(EditionViewModel.class);
        model.setQuizBuilder(quizBuilder);
        model.setStringPool(stringPool);

        // Launch the the two fragments in this activity
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.quiz_overview_container, new EditOverviewFragment())
                .replace(R.id.question_details_container, new EditPreviewFragment())
                .commit();
    }

    @Override
    /* This method is needed to apply the desired language at the activity startup */
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }
}
