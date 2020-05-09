package ch.epfl.qedit.view.online;

import static ch.epfl.qedit.view.LoginActivity.USER;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import ch.epfl.qedit.R;
import ch.epfl.qedit.model.User;
import ch.epfl.qedit.util.LocaleHelper;
import ch.epfl.qedit.view.home.HomeInfoFragment;

public class OnlineActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online);

        User user =
                new User(
                        "you are Online. You can Search the database",
                        "",
                        User.Role.Administrator);

        // Prepare a bundle that contains the user and create a new HomeInfoFragment
        Bundle bundle = new Bundle();
        bundle.putSerializable(USER, user);
        HomeInfoFragment homeInfoFragment = new HomeInfoFragment();
        homeInfoFragment.setArguments(bundle);

        // Create a new OnlineFragment with the user in its Arguments
        OnlineFragment onlineFragment = new OnlineFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.online_info_container, homeInfoFragment)
                .replace(R.id.online_quiz_list_container, onlineFragment)
                .commit();
        // Set page title to display it in the right language

        // TODO Change name
        setTitle(R.string.title_activity_home);
    }

    @Override
    /* This method is needed to apply the desired language at the activity startup */
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }
}
