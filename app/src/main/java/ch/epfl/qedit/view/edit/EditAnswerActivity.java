package ch.epfl.qedit.view.edit;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import ch.epfl.qedit.R;
import ch.epfl.qedit.util.LocaleHelper;

public class EditAnswerActivity extends AppCompatActivity {

    private ImageButton textButton;
    private ImageButton numButton;

    public static final String NUM_DIALOG_TAG = "ch.epfl.qedit.view.edit.NUM_DIALOG_TAG";
    public static final String TEXT_DIALOG_TAG = "ch.epfl.qedit.view.edit.TEXT_DIALOG_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_answer);

        textButton = findViewById(R.id.text_button);
        numButton = findViewById(R.id.number_button);
        setNumButtonListener();
        setTextButtonListener();

    }

    private void setNumButtonListener(){
        numButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        DialogFragment editFieldFragment = EditFieldFragment.newInstance(false);
                        editFieldFragment.show(getSupportFragmentManager(), NUM_DIALOG_TAG);
                    }
                });
    }

    private void setTextButtonListener(){
        textButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        DialogFragment editFieldFragment = EditFieldFragment.newInstance(true);
                        editFieldFragment.show(getSupportFragmentManager(), TEXT_DIALOG_TAG);
                    }
                });
    }

    @Override
    /* This method is needed to apply the desired language at the activity startup */
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }
}
