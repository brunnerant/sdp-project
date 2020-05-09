package ch.epfl.qedit.view.home;

import static ch.epfl.qedit.view.LoginActivity.USER;

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
import ch.epfl.qedit.R;
import ch.epfl.qedit.search.SearchableMapEntry;
import ch.epfl.qedit.backend.database.DatabaseFactory;
import ch.epfl.qedit.backend.database.DatabaseService;
import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.model.StringPool;
import ch.epfl.qedit.model.User;
import ch.epfl.qedit.util.LocaleHelper;
import ch.epfl.qedit.view.quiz.QuizActivity;
import ch.epfl.qedit.view.util.ConfirmDialog;
import ch.epfl.qedit.view.util.EditTextDialog;
import ch.epfl.qedit.view.util.ListEditView;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class HomeQuizListFragment extends Fragment
        implements ConfirmDialog.ConfirmationListener, EditTextDialog.SubmissionListener {
    public static final String QUIZ_ID = "ch.epfl.qedit.view.QUIZ_ID";

    private DatabaseService db;
    private Handler handler;

    private ProgressBar progressBar;

    private ListEditView.Adapter<Map.Entry<String, String>, SearchableMapEntry> listAdapter;

    private ConfirmDialog deleteDialog;
    private EditTextDialog addDialog;
    private int deleteIndex;

    private User user;
    private SearchableMapEntry quizzes = new SearchableMapEntry();

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_home_quiz_list, container, false);

        // Build the top bar and the dialogs
        setHasOptionsMenu(true);
        createDialogs();

        // Get user from the bundle created by the parent activity
        user = (User) Objects.requireNonNull(getArguments()).getSerializable(USER);
        quizzes.e = new ArrayList<>(user.getQuizzes().entrySet().asList());

        // Create the list adapter and bind it to the list edit view
        createAdapter(user);
        ListEditView listEditView = view.findViewById(R.id.home_quiz_list);
        listEditView.setAdapter(listAdapter);

        // The progress bar is needed while waiting from the database
        progressBar = view.findViewById(R.id.quiz_loading);

        // Instantiate Handler and the DatabaseService
        db = DatabaseFactory.getInstance();
        handler = new Handler();

        return view;
    }

    // This function is used to create the list of quizzes for the given user
    private void createAdapter(User user) {
        // Retrieve the quizzes from the user
        quizzes.e = new ArrayList<>(user.getQuizzes().entrySet().asList());

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

    // This is used to create the warning and add dialog
    private void createDialogs() {
        deleteDialog = ConfirmDialog.create(getString(R.string.warning_delete), this);
        addDialog = EditTextDialog.create(getString(R.string.add_quiz_message), this);
        addDialog.setTextFilter(
                new EditTextDialog.TextFilter() {
                    @Override
                    public String isAllowed(String text) {
                        if (text.trim().length() == 0)
                            return getString(R.string.empty_quiz_name_error);

                        for (Map.Entry<String, String> entry : quizzes.e) {
                            if (entry.getValue().equals(text))
                                return getString(R.string.dup_quiz_name_error);
                        }

                        return null;
                    }
                });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_editor_mode, menu);

        MenuItem item = menu.findItem(R.id.app_bar_search);
        SearchView searchView = (SearchView) item.getActionView();

        searchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {
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
        final String quizID = quizzes.e.get(position).getKey();
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

    // This method will be called when the user confirms the deletion by clicking on "yes"
    @Override
    public void onConfirm(ConfirmDialog dialog) {
        if (dialog != deleteDialog) return;

        listAdapter.removeItem(deleteIndex);
    }

    // This method will be called when the user confirms the addition by clicking "yes"
    @Override
    public void onSubmit(String text) {
        listAdapter.addItem(new AbstractMap.SimpleEntry<>("key", text));
    }
}
