package ch.epfl.qedit.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import ch.epfl.qedit.R;
import ch.epfl.qedit.ui.login.LoginActivity;

public class ViewRoleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_role);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String username = intent.getStringExtra(LoginActivity.EXTRA_MESSAGE);
        String message = "Bienvenue " + username + "!";

        // Capture the layout's TextView and set the string as its text
        TextView textViewWelcome = findViewById(R.id.greeting);
        textViewWelcome.setText(message);

        TextView textViewRole = findViewById(R.id.role);
        if(username.equals("admin")) {
            textViewRole.setText("Vous êtes un administrateur.");
        } else {
            textViewRole.setText("Vous êtes un participant.");
        }
    }
}
