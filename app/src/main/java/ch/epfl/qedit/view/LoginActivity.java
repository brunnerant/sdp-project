package ch.epfl.qedit.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import ch.epfl.qedit.R;

public class LoginActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "ch.epfl.qedit.view.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void loginButton(View view) {
        EditText editTextUsername = findViewById(R.id.username);
        String username = editTextUsername.getText().toString();
        Intent intent = new Intent(LoginActivity.this, ViewRoleActivity.class);
        intent.putExtra(EXTRA_MESSAGE, username);
        startActivity(intent);
    }
}
