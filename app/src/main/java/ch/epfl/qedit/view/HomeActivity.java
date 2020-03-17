package ch.epfl.qedit.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import ch.epfl.qedit.R;
import ch.epfl.qedit.model.User;
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
        String message = "Bienvenue " + Objects.requireNonNull(user).getFullName() + " !";

        // Capture the layout's TextView and set the string as its text
        TextView textViewWelcome = findViewById(R.id.greeting);
        textViewWelcome.setText(message);

        TextView textViewRole = findViewById(R.id.role);

        textViewRole.setText(getRoleText(user.getRole()));
    }

    private String getRoleText(User.Role role) {
        String roleText = "Vous êtes un ";
        switch (role) {
            case Participant:
                roleText += "participant.";
                break;
            case Administrator:
                roleText += "administrateur.";
                break;
            case Editor:
                roleText += "éditeur.";
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
