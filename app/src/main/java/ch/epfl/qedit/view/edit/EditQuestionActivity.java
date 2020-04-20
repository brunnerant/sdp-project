package ch.epfl.qedit.view.edit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.widget.EditText;

import ch.epfl.qedit.R;
import ch.epfl.qedit.viewmodel.EditionViewModel;
import ch.epfl.qedit.viewmodel.QuizViewModel;

public class EditQuestionActivity extends AppCompatActivity {

    private EditionViewModel model;
    private EditText editTitle;
    private EditText editText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_question);

        editTitle = findViewById(R.id.edit_question_title);
        editText = findViewById(R.id.edit_question_text);

        // this need to be replace by this.getParent()
        model = new ViewModelProvider(this).get(EditionViewModel.class);
        model.initQuestionBuilder();
    }


}
