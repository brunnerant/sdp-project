package ch.epfl.qedit.view.home;

import static android.app.Activity.RESULT_OK;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static ch.epfl.qedit.model.StringPool.TITLE_ID;
import static ch.epfl.qedit.view.login.Util.USER;

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
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import ch.epfl.qedit.R;
import ch.epfl.qedit.backend.database.DatabaseFactory;
import ch.epfl.qedit.backend.database.DatabaseService;
import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.model.StringPool;
import ch.epfl.qedit.model.User;
import ch.epfl.qedit.util.LocaleHelper;
import ch.epfl.qedit.view.edit.EditSettingsActivity;
import ch.epfl.qedit.view.login.LogInActivity;
import ch.epfl.qedit.view.login.Util;
import ch.epfl.qedit.view.util.ConfirmDialog;
import ch.epfl.qedit.view.util.EditTextDialog;
import ch.epfl.qedit.view.util.ListEditView;
import com.google.firebase.auth.FirebaseAuth;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class HomeQuizListFragment extends Fragment
        implements ConfirmDialog.ConfirmationListener, EditTextDialog.SubmissionListener {
    public static final String QUIZ_ID = "ch.epfl.qedit.view.QUIZ_ID";
    public static final String STRING_POOL = "ch.epfl.qedit.view.STRING_POOL";
    public static final int EDIT_QUIZ_REQUEST_CODE = 2;

    private DatabaseService db;
    private Handler handler;

    private ProgressBar progressBar;
    private ListEditView.Adapter<Map.Entry<String, String>> listAdapter;

    private ConfirmDialog deleteDialog;
    private EditTextDialog addDialog;
    private int deleteIndex;

    private User user;
    private List<Map.Entry<String, String>> quizzes;

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
        quizzes = new ArrayList<>(user.getQuizzes().entrySet().asList());

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
        quizzes = new ArrayList<>(user.getQuizzes().entrySet().asList());

        // Create the list adapter
        listAdapter = new ListEditView.Adapter<>(quizzes, Map.Entry::getValue);

        // Listen to the data changes
        listAdapter.setItemListener(
                (position, type) -> {
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
                });
    }

    // This is used to create the warning and add dialog
    private void createDialogs() {
        deleteDialog = ConfirmDialog.create(getString(R.string.warning_delete), this);
        addDialog = EditTextDialog.create(getString(R.string.add_quiz_message), this);
        addDialog.setTextFilter(
                text -> {
                    if (text.trim().length() == 0) return getString(R.string.empty_quiz_name_error);

                    for (Map.Entry<String, String> entry : quizzes) {
                        if (entry.getValue().equals(text))
                            return getString(R.string.dup_quiz_name_error);
                    }

                    return null;
                });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.home_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    // This method will be called when an item of the top bar is clicked on
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                addDialog.show(getParentFragmentManager(), "add_dialog");
                break;
            case R.id.log_out:
                logOut();
                break;
            case android.R.id.home:
                requireActivity().onBackPressed();
                break;
        }

        return true;
    }

    private void logOut() {
        // Retrieve cached user id
        String uid = Util.getStringInPrefs(getActivity(), "user_id");

        // Remove cached user id
        Util.removeStringInPrefs(getActivity(), "user_id");

        // Log out
        FirebaseAuth.getInstance().signOut();

        // Go to log in activity
        startActivity(new Intent(getActivity(), LogInActivity.class));
    }

    // Handles when a user clicked on the button to remove a quiz
    private void deleteConfirmation(int position) {
        deleteDialog.show(getParentFragmentManager(), "delete_confirmation");
        deleteIndex = position;
    }

    // Handles when a user clicked on the button to show a quiz
    //    private void showQuiz(int position) { // TODO next sprint
    //        final String quizID = quizzes.get(position).getKey();
    //        progressBar.setVisibility(VISIBLE);
    //
    //        CompletableFuture<StringPool> stringPool =
    //                db.getQuizLanguages(quizID)
    //                        .thenCompose(
    //                                languages ->
    //                                        db.getQuizStringPool(quizID,
    // getBestLanguage(languages)));
    //
    //        CompletableFuture<Quiz> quizStructure = db.getQuizStructure(quizID);
    //
    //        CompletableFuture.allOf(stringPool, quizStructure)
    //                .whenComplete(
    //                        (aVoid, throwable) -> {
    //                            if (throwable != null)
    //                                Toast.makeText(
    //                                                requireContext(),
    //                                                R.string.database_error,
    //                                                Toast.LENGTH_SHORT)
    //                                        .show();
    //                            else
    //                                launchQuizActivity(
    //                                        quizStructure
    //                                                .join()
    //                                                .instantiateLanguage(stringPool.join()));
    //                        });
    //
    //    }

    // Handles when a user clicked on the button to edit a quiz
    private void editQuiz(int position) {
        final String quizID = quizzes.get(position).getKey();
        progressBar.setVisibility(VISIBLE);

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
                                launchEditSettingsActivity(quizStructure.join(), stringPool.join());
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
    //    private void launchQuizActivity(Quiz quiz) { TODO next sprint
    //        Intent intent = new Intent(requireActivity(), QuizActivity.class);
    //        Bundle bundle = new Bundle();
    //        bundle.putSerializable(QUIZ_ID, quiz);
    //        intent.putExtras(bundle);
    //        startActivity(intent);
    //    }

    // Launches the EditSettingsActivity with the given quiz. This is used when a quiz is either
    // added or modified.
    private void launchEditSettingsActivity(Quiz quiz, StringPool stringPool) {
        Intent intent = new Intent(requireActivity(), EditSettingsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(STRING_POOL, stringPool);

        if (quiz != null) {
            bundle.putSerializable(QUIZ_ID, quiz);
        }

        intent.putExtras(bundle);
        startActivityForResult(intent, EDIT_QUIZ_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_QUIZ_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Get the quiz and the extended StringPool from the returned data
                Quiz quiz = (Quiz) data.getExtras().getSerializable(QUIZ_ID);
                StringPool extendedStringPool =
                        (StringPool) data.getExtras().getSerializable(STRING_POOL);

                // Hide progress bar
                progressBar.setVisibility(GONE);
                // TODO send back to data base etc.
            }
        }
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
        StringPool stringPool = new StringPool();
        stringPool.update(TITLE_ID, text);
        launchEditSettingsActivity(null, stringPool);
    }
}
