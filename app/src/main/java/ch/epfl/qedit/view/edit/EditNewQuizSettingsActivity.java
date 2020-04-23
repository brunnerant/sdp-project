package ch.epfl.qedit.view.edit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import ch.epfl.qedit.R;
import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.model.StringPool;

public class EditNewQuizSettingsActivity extends AppCompatActivity {
    public static final String QUIZ_BUILDER = "ch.epfl.qedit.model.QUIZ_BUILDER";
    public static final String STRING_POOL = "ch.epfl.qedit.model.STRING_POOL";

    private Quiz.Builder quizBuilder;
    private StringPool stringPool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_new_quiz_settings);

        quizBuilder = new Quiz.Builder();
        stringPool = new StringPool();

        stringPool.put(StringPool.TITLE_ID, "Test");
    }

    public void startEditing(View view) {
        Intent intent = new Intent(EditNewQuizSettingsActivity.this, EditQuizActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(QUIZ_BUILDER, quizBuilder);
        bundle.putSerializable(STRING_POOL, stringPool);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
