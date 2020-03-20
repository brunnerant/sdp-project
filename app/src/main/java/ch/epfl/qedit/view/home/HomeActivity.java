package ch.epfl.qedit.view.home;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import ch.epfl.qedit.R;
import ch.epfl.qedit.model.User;
import ch.epfl.qedit.view.LoginActivity;
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

        // Prepare a bundle that contains the user and create a new HomeInfoFragment
        Bundle bundle = new Bundle();
        bundle.putSerializable("user", user);
        HomeInfoFragment homeInfoFragment = new HomeInfoFragment();
        homeInfoFragment.setArguments(bundle);

        // Create a new HomeQuizListFragment with the user in its Arguments
        HomeQuizListFragment homeQuizListFragment = new HomeQuizListFragment();
        homeQuizListFragment.setArguments(bundle);

        // Start the two fragments that are contained in this activity
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.home_info_container, homeInfoFragment)
                .replace(R.id.home_quiz_list_container, homeQuizListFragment)
                .commit();
    }
}
