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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_answer);

        ImageButton textButton = (ImageButton) findViewById(R.id.text_button);
        textButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        DialogFragment editFieldFragment = new EditFieldFragment(true);
                        editFieldFragment.show(getSupportFragmentManager(), "edit");
                    }
                });
    }

    @Override
    /* This method is needed to apply the desired language at the activity startup */
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }
}
