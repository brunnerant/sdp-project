package ch.epfl.qedit.view.edit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import android.widget.EditText;
import android.widget.ImageButton;

import ch.epfl.qedit.R;
import ch.epfl.qedit.model.Question;
import ch.epfl.qedit.model.answer.AnswerFormat;
import ch.epfl.qedit.util.LocaleHelper;

public class EditQuestionActivity extends AppCompatActivity {

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

        //questionBuilder = (Question.Builder) savedInstanceState.get(EditQuizActivity.QUESTION_BUILDER);

        ImageButton addButton = (ImageButton) findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                switchToEditAnswerActivity();
            }
        });

    }

    private void switchToEditAnswerActivity(){
        Intent intent = new Intent(EditQuestionActivity.this, EditAnswerActivity.class);
        startActivity(intent);
    }

    @Override
    /* This method is needed to apply the desired language at the activity startup */
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }
}
