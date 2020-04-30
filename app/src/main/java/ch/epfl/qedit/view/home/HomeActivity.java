package ch.epfl.qedit.view.home;

import static ch.epfl.qedit.view.LoginActivity.USER;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import ch.epfl.qedit.R;
import ch.epfl.qedit.model.User;
import ch.epfl.qedit.util.LocaleHelper;
import ch.epfl.qedit.view.sensor.ScannerActivity;
import com.google.android.material.navigation.NavigationView;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        User user = (User) Objects.requireNonNull(intent.getExtras()).getSerializable(USER);

        // create hamburger menu
        createDrawer();

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

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            drawer.openDrawer(GravityCompat.START);
        }
    }

    // manage selection of items in navigation drawer
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.burger_home:
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.my_quizzes:
                Toast.makeText(this, "Can't see your quizzes for now", Toast.LENGTH_SHORT).show();
                break;
            case R.id.online_quizzes:
                Toast.makeText(this, "Can't find online quizzes for now", Toast.LENGTH_SHORT)
                        .show();
                break;
            case R.id.qr_code_burger:
                Intent intent = new Intent(HomeActivity.this, ScannerActivity.class);
                startActivity(intent);
                break;
            case R.id.my_account:
                Toast.makeText(this, "You don't have an account page for now", Toast.LENGTH_SHORT)
                        .show();
                break;
            case R.id.settings:
                Toast.makeText(this, "Can't change settings for now", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    private void createDrawer() {
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.burger_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle =
                new ActionBarDrawerToggle(
                        this,
                        drawer,
                        R.string.navigation_drawer_open,
                        R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }
}
