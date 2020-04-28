package ch.epfl.qedit.view.edit;

import static ch.epfl.qedit.view.edit.EditNewQuizSettingsActivity.STRING_POOL;
import static ch.epfl.qedit.view.edit.EditOverviewFragment.QUESTION_BUILDER;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import ch.epfl.qedit.R;
import ch.epfl.qedit.model.Question;
import ch.epfl.qedit.model.StringPool;
import ch.epfl.qedit.model.answer.AnswerFormat;
import ch.epfl.qedit.model.answer.MatrixFormat;
import ch.epfl.qedit.util.LocaleHelper;
import java.util.Objects;

public class EditQuestionActivity extends AppCompatActivity {
    public static final String QUESTION = "ch.epfl.qedit.view.edit.QUESTION";

    private EditText editTitle;
    private EditText editText;
    private Question.Builder questionBuilder;
    private AnswerFormat answerFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_question);

        editTitle = findViewById(R.id.edit_question_title);
        editText = findViewById(R.id.edit_question_text);

        /**
        Intent intent = getIntent();
        questionBuilder =
                (Question.Builder)
                        Objects.requireNonNull(intent.getExtras())
                                .getSerializable(QUESTION_BUILDER);
        StringPool stringPool =
                (StringPool)
                        Objects.requireNonNull(intent.getExtras()).getSerializable(STRING_POOL);
        **/
        ImageButton addButton = (ImageButton) findViewById(R.id.add_button);
        addButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        switchToEditAnswerActivity();
                    }
                });
        /**
        questionBuilder.setTitleID(stringPool.put("This is a test title"));
        questionBuilder.setTextID(stringPool.put("This is a test text"));
        questionBuilder.setFormat(MatrixFormat.singleField(MatrixFormat.Field.textField("", 25)));

        intent = new Intent();
        intent.putExtra(QUESTION, questionBuilder.build());
        setResult(RESULT_OK, intent);
        finish(); **/
    }

    private void switchToEditAnswerActivity() {
        Intent intent = new Intent(EditQuestionActivity.this, EditAnswerActivity.class);
        startActivity(intent);
    }

    @Override
    /* This method is needed to apply the desired language at the activity startup */
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }
}
