package ch.epfl.qedit.view.home;

import static ch.epfl.qedit.view.LoginActivity.USER;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
        User user = (User) Objects.requireNonNull(intent.getExtras()).getSerializable(USER);

        // Prepare a bundle that contains the user and create a new HomeInfoFragment
        Bundle bundle = new Bundle();
        bundle.putSerializable(USER, user);
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

        // Set page title to display it in the right language
        setTitle(R.string.title_activity_home);
    }

    @Override
    /* This method is needed to apply the desired language at the activity startup */
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }
}
