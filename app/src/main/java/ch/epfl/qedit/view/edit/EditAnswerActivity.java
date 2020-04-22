package ch.epfl.qedit.view.edit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;

import ch.epfl.qedit.R;
import ch.epfl.qedit.util.LocaleHelper;

public class EditAnswerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_answer);
    }

    @Override
    /* This method is needed to apply the desired language at the activity startup */
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }


}
