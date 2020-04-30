package ch.epfl.qedit.view.Online;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import ch.epfl.qedit.R;
import ch.epfl.qedit.Search.SearchableMapEntry;
import ch.epfl.qedit.Search.SearchablePair;
import ch.epfl.qedit.backend.database.DatabaseFactory;
import ch.epfl.qedit.backend.database.DatabaseService;
import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.model.User;
import ch.epfl.qedit.util.Callback;
import ch.epfl.qedit.util.LocaleHelper;
import ch.epfl.qedit.util.Response;
import ch.epfl.qedit.view.home.HomeInfoFragment;
import ch.epfl.qedit.view.home.HomeQuizListFragment;
import ch.epfl.qedit.view.util.ListEditView;

import static ch.epfl.qedit.view.LoginActivity.USER;

public class OnlineActivity extends AppCompatActivity{
    private ListEditView.Adapter<Map.Entry<String, String>, SearchableMapEntry> listAdapter;
    private DatabaseService db = DatabaseFactory.getInstance();
    private List<String> languages;
    private SearchablePair<Quiz> all;
    private Map<String, String> stringPool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();

        //db.searchDatabase(0);

        User user = new User("Online", "", User.Role.Administrator);

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

    public void setAdapter(ListEditView.Adapter<Map.Entry<String, String>, SearchableMapEntry> listAdapter) {
        this.listAdapter = listAdapter;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);

        MenuItem item = menu.findItem(R.id.app_bar_search);
        SearchView searchView = (SearchView) item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                listAdapter.getFilter().filter(newText);
                return false;
            }
        });

        return true;
    }

    private void createAdapter(User user) {
        all.e = new ArrayList<>();

        // Create the list adapter
        listAdapter =
                new ListEditView.Adapter<>(
                        all,
                        new ListEditView.GetItemText<Map.Entry<String, String>>() {
                            @Override
                            public String getText(Map.Entry<String, String> item) {
                                return item.getValue();
                            }
                        });
    }
}
