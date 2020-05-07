package ch.epfl.qedit.view.Online;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import ch.epfl.qedit.R;
import ch.epfl.qedit.Search.SearchableMapEntry;
import ch.epfl.qedit.backend.database.DatabaseFactory;
import ch.epfl.qedit.backend.database.DatabaseService;
import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.model.StringPool;
import ch.epfl.qedit.model.User;
import ch.epfl.qedit.util.LocaleHelper;
import ch.epfl.qedit.view.home.HomeActivity;
import ch.epfl.qedit.view.quiz.QuizActivity;
import ch.epfl.qedit.view.util.ConfirmDialog;
import ch.epfl.qedit.view.util.EditTextDialog;
import ch.epfl.qedit.view.util.ListEditView;

import static ch.epfl.qedit.view.LoginActivity.USER;

public class OnlineFragment extends Fragment {
    public static final String QUIZ_ID = "ch.epfl.qedit.view.QUIZ_ID";

    private DatabaseService db;
    private Handler handler;

    private ProgressBar progressBar;

    private ListEditView.Adapter<Map.Entry<String, String>, SearchableMapEntry> listAdapter;

    private ConfirmDialog deleteDialog;
    private EditTextDialog addDialog;
    private ListEditView listEditView;
    private int deleteIndex;
    private int load = 0;

    private SearchableMapEntry quizzes = new SearchableMapEntry();
    private List<Map.Entry<String, String>> quizzes2 = new ArrayList<>();

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_home_quiz_list, container, false);

        // Build the top bar and the dialogs
        setHasOptionsMenu(true);
        // Get user from the bundle created by the parent activity
        //user = (User) Objects.requireNonNull(getArguments()).getSerializable(USER);
        //quizzes.e = new ArrayList<>(user.getQuizzes().entrySet().asList());

        // Create the list adapter and bind it to the list edit view
        createAdapter();
        listEditView = view.findViewById(R.id.home_quiz_list);
        listEditView.setAdapter(listAdapter);
        listEditView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    System.out.println("Wiuaebiuaeb iaeub aieubaeiub aeivu aviuae vbieau beieua bib iuv iav bia vbeai eavie aaei bveuiae iae beaieb i");
                }
            }
        });
        // The progress bar is needed while waiting from the database
        progressBar = view.findViewById(R.id.quiz_loading);

        // Instantiate Handler and the DatabaseService
        db = DatabaseFactory.getInstance();
        handler = new Handler();
        return view;
    }

    // This function is used to create the list of quizzes for the given user
    private void createAdapter() {
        // Retrieve the quizzes from the user
        quizzes.e = new ArrayList<>();

        // Create the list adapter
        listAdapter =
                new ListEditView.Adapter<>(
                        quizzes,
                        new ListEditView.GetItemText<Map.Entry<String, String>>() {
                            @Override
                            public String getText(Map.Entry<String, String> item) {
                                return item.getValue();
                            }
                        });

        // Listen to the data changes
        listAdapter.setItemListener(
                new ListEditView.ItemListener() {
                    @Override
                    public void onItemEvent(int position, ListEditView.EventType type) {
                        switch (type) {
                            case RemoveRequest:
                                deleteConfirmation(position);
                                break;
                            case EditRequest:
                                editQuiz(position);
                                break;
                            default:
                                break;
                        }
                    }
                });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //inflater.inflate(R.menu.menu_editor_mode, menu);
        inflater.inflate(R.menu.search, menu);

        MenuItem item = menu.findItem(R.id.app_bar_search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                try {
                    listAdapter.clear();
                    quizzes2 = db.searchDatabase(load, load + 10, query).get();
                    for(Map.Entry<String, String> e: quizzes2) {
                        listAdapter.addItem(e);
                    }
                    load = 10;
                    searchView.clearFocus();
                } catch (ExecutionException e) {
                } catch (InterruptedException e) {
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                load = 0;
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    // This method will be called when an item of the top bar is clicked on
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                addDialog.show(getParentFragmentManager(), "add_dialog");
                break;
            case android.R.id.home:
                requireActivity().onBackPressed();
                break;
        }

        return true;
    }

    // Handles when a user clicked on the button to remove a quiz
    private void deleteConfirmation(int position) {
        deleteDialog.show(getParentFragmentManager(), "delete_confirmation");
        deleteIndex = position;
    }

    // Handles when a user clicked on the button to edit a quiz
    private void editQuiz(int position) {
        final String quizID = quizzes2.get(position).getKey();
        progressBar.setVisibility(View.VISIBLE);

        CompletableFuture<StringPool> stringPool =
                db.getQuizLanguages(quizID)
                        .thenCompose(
                                languages ->
                                        db.getQuizStringPool(quizID, getBestLanguage(languages)));

        CompletableFuture<Quiz> quizStructure = db.getQuizStructure(quizID);

        CompletableFuture.allOf(stringPool, quizStructure)
                .whenComplete(
                        (aVoid, throwable) -> {
                            if (throwable != null)
                                Toast.makeText(
                                        requireContext(),
                                        R.string.database_error,
                                        Toast.LENGTH_SHORT)
                                        .show();
                            else
                                launchQuizActivity(
                                        quizStructure
                                                .join()
                                                .instantiateLanguage(stringPool.join()));
                        });
    }

    private String getBestLanguage(List<String> languages) {
        String appLanguage = LocaleHelper.getLanguage(requireContext());

        // If the quiz was translated in the application language, pick that version,
        // otherwise we pick the first one. Note that we could have some more complex logic
        // here, for example trying english first, and then falling back.
        if (languages.contains(appLanguage)) return appLanguage;
        else return languages.get(0);
    }

    // Launches the quiz activity with the given quiz. This is used when a quiz is selected.
    private void launchQuizActivity(Quiz quiz) {
        Intent intent = new Intent(requireActivity(), QuizActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(QUIZ_ID, quiz);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
