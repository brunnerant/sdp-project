package ch.epfl.qedit.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

import ch.epfl.qedit.R;
import ch.epfl.qedit.model.User;

public class HomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        User user = (User) Objects.requireNonNull(intent.getExtras()).getSerializable(LoginActivity.USER);
        String message = "Bienvenue " + Objects.requireNonNull(user).getFullName() + " !";

        // Capture the layout's TextView and set the string as its text
        TextView textViewWelcome = findViewById(R.id.greeting);
        textViewWelcome.setText(message);

        TextView textViewRole = findViewById(R.id.role);
        String roleText = "";
        switch (user.getRole()) {
            case Participant:
                roleText = "Vous êtes un participant.";
                break;
            case Administrator:
                roleText = "Vous êtes un administrateur.";
                break;
            case Editor:
                roleText = "Vous êtes un éditeur.";
                break;
        }

        textViewRole.setText(roleText);
    }

    public void goToQuizActivity(View view) {
        Intent intent = new Intent(this, QuizActivity.class);
        startActivity(intent);
    }
}
