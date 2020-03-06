package ch.epfl.qedit.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import ch.epfl.qedit.R;

public class ViewRoleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_role);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String username = intent.getStringExtra(LoginActivity.EXTRA_MESSAGE);
        String message = "Bienvenue " + username + " !";

        // Capture the layout's TextView and set the string as its text
        TextView textViewWelcome = findViewById(R.id.greeting);
        textViewWelcome.setText(message);

        TextView textViewRole = findViewById(R.id.role);
        if (username != null && username.equals("admin")) {
            textViewRole.setText("@string/youAreAdmin");
        } else {
            textViewRole.setText("@string/youAreParticipant");
        }
    }
}
