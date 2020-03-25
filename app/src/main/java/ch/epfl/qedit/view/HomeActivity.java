package ch.epfl.qedit.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import ch.epfl.qedit.R;
import ch.epfl.qedit.model.User;
import ch.epfl.qedit.util.LocaleHelper;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        User user =
                (User)
                        Objects.requireNonNull(intent.getExtras())
                                .getSerializable(LoginActivity.USER);
        String message =
                getResources().getString(R.string.welcome)
                        + " "
                        + Objects.requireNonNull(user).getFullName()
                        + getResources().getString(R.string.exclamation_point);

        // Capture the layout's TextView and set the string as its text
        TextView textViewWelcome = findViewById(R.id.greeting);
        textViewWelcome.setText(message);

        TextView textViewRole = findViewById(R.id.role);

        textViewRole.setText(getRoleText(user.getRole()));

        setTitle(R.string.label_home);
    }

    @Override
    /** This method is needed to apply the desired language at the activity startup */
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    private String getRoleText(User.Role role) {
        String roleText = "";
        switch (role) {
            case Administrator:
                roleText = getResources().getString(R.string.role_administrator);
                break;
            case Editor:
                roleText = getResources().getString(R.string.role_editor);
                break;
            case Participant:
                roleText = getResources().getString(R.string.role_participant);
                break;
            default:
                break;
        }

        return roleText;
    }

    public void goToQuizActivity(View view) {
        Intent intent = new Intent(this, QuizActivity.class);
        startActivity(intent);
    }
}
