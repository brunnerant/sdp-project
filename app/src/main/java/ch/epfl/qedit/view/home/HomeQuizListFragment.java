package ch.epfl.qedit.view.home;

import static android.app.Activity.RESULT_OK;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static ch.epfl.qedit.model.StringPool.TITLE_ID;
import static ch.epfl.qedit.view.edit.EditQuizSettingsDialog.NO_FILTER;
import static ch.epfl.qedit.view.edit.EditQuizSettingsDialog.QUIZ_BUILDER;
import static ch.epfl.qedit.view.home.HomeActivity.USER;

import android.content.Intent;
import android.os.Bundle;
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
import ch.epfl.qedit.backend.auth.AuthenticationFactory;
import ch.epfl.qedit.backend.database.DatabaseFactory;
import ch.epfl.qedit.backend.database.DatabaseService;
import ch.epfl.qedit.backend.database.Util;
import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.model.StringPool;
import ch.epfl.qedit.model.User;
import ch.epfl.qedit.view.edit.EditQuizActivity;
import ch.epfl.qedit.view.edit.EditQuizSettingsDialog;
import ch.epfl.qedit.view.quiz.QuizActivity;
import ch.epfl.qedit.view.treasurehunt.TreasureHuntActivity;
import ch.epfl.qedit.view.util.ConfirmDialog;
import ch.epfl.qedit.view.util.ListEditView;
import com.google.common.collect.ImmutableMap;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;

public class HomeQuizListFragment extends Fragment
        implements ConfirmDialog.ConfirmationListener, EditQuizSettingsDialog.SubmissionListener {

    public static final String QUIZ_ID = "ch.epfl.qedit.view.QUIZ_ID";
    public static final String STRING_POOL = "ch.epfl.qedit.view.STRING_POOL";
    public static final int EDIT_NEW_QUIZ_REQUEST_CODE = 2;
    private static final int EDIT_EXISTING_QUIZ_REQUEST_CODE = 3;

    private DatabaseService db;

    private ProgressBar progressBar;
    private ListEditView.Adapter<Map.Entry<String, String>> listAdapter;

    private ConfirmDialog deleteDialog;
    private EditQuizSettingsDialog.TextFilter textFilter = NO_FILTER;

    // The indices of the quizzes that are being deleted and modified, respectively
    private int deleteIndex = -1;
    private int modifyIndex = -1;

    // The user and its quizzes (id and title of the quizzes)
    private User user;
    private List<Map.Entry<String, String>> quizzes;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_home_quiz_list, container, false);

        // Set the top bar
        setHasOptionsMenu(true);

        // Initialize the dialog shown on deletion
        deleteDialog = ConfirmDialog.create(getString(R.string.warning_delete_quiz), this);

        // Create the filter that is applied on the titles enter by the user when changing the title
        // of the quiz
        createTextFilter();

        // Get user from the bundle created by the parent activity and get his/her quizzes
        user = (User) Objects.requireNonNull(getArguments()).getSerializable(USER);
        quizzes = new ArrayList<>(user.getQuizzes().entrySet().asList());

        // Create the list adapter and bind it to the list edit view
        createAdapter(user);
        ListEditView listEditView = view.findViewById(R.id.home_quiz_list);
        listEditView.setAdapter(listAdapter);

        // The progress bar is needed while waiting from the database
        progressBar = view.findViewById(R.id.quiz_loading);

        // Instantiate Handler and the DatabaseService
        db = DatabaseFactory.getInstance(requireContext());

        return view;
    }

    /** Initialize the TextFilter used to check the titles entered by the user */
    private void createTextFilter() {
        textFilter =
                text -> {
                    // Empty
                    if (text.trim().length() == 0) return getString(R.string.empty_quiz_name_error);

                    for (Map.Entry<String, String> entry : quizzes) {
                        if (entry.getValue().equals(text))
                            return getString(R.string.dup_quiz_name_error);
                    }

                    return null; // null = no error occurred
                };
    }

    // This function is used to create the list of quizzes for the given user
    private void createAdapter(User user) {
        // Those are the items of the popup menu
        List<String> popupMenuItems =
                Arrays.asList(getString(R.string.menu_edit), getString(R.string.menu_delete));

        // Create the list adapter
        listAdapter = new ListEditView.Adapter<>(quizzes, Map.Entry::getValue, popupMenuItems);

        // Listen to the data changes
        listAdapter.setItemListener(
                (position, code) -> {
                    if (code == ListEditView.ItemListener.CLICK) startQuiz(position);
                    else if (code == 0) // edit was clicked
                    editQuiz(position);
                    else if (code == 1) // delete was clicked
                    deleteConfirmation(position);
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
                // Prepare and show the dialog with settings for a new quiz
                EditQuizSettingsDialog addSettingsDialog = EditQuizSettingsDialog.newInstance(this);
                addSettingsDialog.setTextFilter(textFilter);
                addSettingsDialog.show(getParentFragmentManager(), "add_dialog");
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

    // Handles when a user clicked on the button to show a quiz
    private void startQuiz(int position) {
        final String quizID = quizzes.get(position).getKey();
        progressBar.setVisibility(VISIBLE);
        withQuiz(quizID, (quiz, pool) -> launchQuizActivity(quiz.instantiateLanguage(pool)));
    }

    // Handles when a user clicked on the button to edit a quiz
    private void editQuiz(int position) {
        final String quizID = quizzes.get(position).getKey();
        progressBar.setVisibility(VISIBLE);
        withQuiz(quizID, (quiz, pool) -> launchModifyQuizDialog(pool, quiz, position));
    }

    // Loads a quiz from the database and performs the given action once it arrives
    private void withQuiz(String quizId, BiConsumer<Quiz, StringPool> action) {
        Util.getQuiz(db, quizId, requireContext())
                .whenComplete(
                        (pair, throwable) ->
                                requireActivity()
                                        .runOnUiThread(
                                                () -> {
                                                    // Hide progress bar
                                                    progressBar.setVisibility(GONE);

                                                    if (throwable != null) {
                                                        Toast.makeText(
                                                                        requireContext(),
                                                                        R.string.database_error,
                                                                        Toast.LENGTH_SHORT)
                                                                .show();
                                                    } else {
                                                        action.accept(pair.first, pair.second);
                                                    }
                                                }));
    }

    /**
     * Initializes a dialog that gives the user the possibility to change the title of the already
     * existing quiz
     */
    private void launchModifyQuizDialog(StringPool stringPool, Quiz quizStructure, int position) {
        EditQuizSettingsDialog modifySettingsDialog =
                EditQuizSettingsDialog.newInstance(this, stringPool, quizStructure);
        modifySettingsDialog.setTextFilter(textFilter);
        modifySettingsDialog.show(getParentFragmentManager(), "modify_dialog");
        modifyIndex = position;
    }

    // Launches the quiz activity with the given quiz. This is used when a quiz is selected.
    private void launchQuizActivity(Quiz quiz) {
        // Depending on the type of quiz, we might need to go to the treasure hunt activity
        Class targetClass = quiz.isTreasureHunt() ? TreasureHuntActivity.class : QuizActivity.class;
        Intent intent = new Intent(requireActivity(), targetClass);

        // We put the quiz into the bundle
        Bundle bundle = new Bundle();
        bundle.putSerializable(QUIZ_ID, quiz);
        intent.putExtras(bundle);

        // And start the activity
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            // Get the quiz and the extended StringPool from the returned data
            Quiz quiz = (Quiz) data.getExtras().getSerializable(QUIZ_ID);
            StringPool extendedStringPool =
                    (StringPool) data.getExtras().getSerializable(STRING_POOL);

            // Get the title of the quiz
            String title = extendedStringPool.get(TITLE_ID);

            if (requestCode == EDIT_NEW_QUIZ_REQUEST_CODE) {
                // The user edited an new quiz
                handleNewQuizResult(quiz, extendedStringPool, title);
            } else if (requestCode == EDIT_EXISTING_QUIZ_REQUEST_CODE) {
                // The user edited an already existing Quiz
                handleModifyQuizResult(title);
            }

            // When the user decides to stop the edition without saving the changes the
            // EditQuizActivity will return with RESULT_CANCELED. But we do not need to do anything
            // in this case.
        }
    }

    /** Handles the case where the user edited a new quiz */
    private void handleNewQuizResult(Quiz quiz, StringPool stringPool, String title) {
        // Upload the new quiz and stringPool to the database
        // We should handle the case when a quiz fails to upload to the database here
        db.uploadQuiz(quiz, stringPool)
                .whenComplete(
                        (quizId, throwable) ->
                                requireActivity()
                                        .runOnUiThread(() -> onUpload(quizId, title, throwable)));
    }

    // Runs when a quiz has been successfully uploaded
    private void onUpload(String quizId, String title, Throwable throwable) {
        if (throwable != null) {
            // In case of an error we just inform the user
            Toast.makeText(requireContext(), R.string.database_upload_failed, Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        // Otherwise, we inform the user that the upload was successful
        Toast.makeText(requireContext(), R.string.database_upload_successful, Toast.LENGTH_SHORT)
                .show();

        // Extend the list of quizzes of the user
        listAdapter.addItem(new AbstractMap.SimpleEntry<>(quizId, title));

        // Add the quiz to the local user
        user.addQuiz(quizId, title);

        // And update the list of quizzes of the user
        String userId = AuthenticationFactory.getInstance().getUser();
        db.updateUserQuizList(userId, ImmutableMap.copyOf(quizzes));
    }

    /** Handles the case where the user edited an already existing quiz */
    private void handleModifyQuizResult(String title) {
        // First get the old entry
        Map.Entry<String, String> oldEntry = quizzes.get(modifyIndex);
        String quizId = oldEntry.getKey();

        // Updating the quiz in the database is not yet supported, so for now the changes only
        // occur locally.

        // Update the existing entry in the list of quizzes
        listAdapter.updateItem(modifyIndex, new AbstractMap.SimpleImmutableEntry<>(quizId, title));
        modifyIndex = -1;

        // Update the quiz list of the local user
        user.updateQuizTitle(quizId, title);

        Toast.makeText(
                        requireContext(),
                        "Updating a quiz in the database is not yet supported, so the changes will only be visible locally.",
                        Toast.LENGTH_SHORT)
                .show();
    }

    // This method will be called when the user confirms the deletion by clicking on "yes"
    @Override
    public void onConfirm(ConfirmDialog dialog) {
        if (dialog != deleteDialog) return;

        // We remove the quiz from the list view
        listAdapter.removeItem(deleteIndex);
        deleteIndex = -1;

        // And update the list of quizzes of the user
        String userId = AuthenticationFactory.getInstance().getUser();
        db.updateUserQuizList(userId, ImmutableMap.copyOf(quizzes));

        // As a note, the way we currently handle deletion means that the user will not see the
        // quiz anymore, but it will persist in the database. This is satisfactory for now, since
        // deleting the quiz from the database could create problems in case other users also
        // downloaded the same quiz.
    }

    // This method will be called when the user submits the settings made in the SettingsDialog by
    // clicking on "Start editing"
    @Override
    public void onSubmit(StringPool stringPool, Quiz.Builder quizBuilder) {
        int requestCode;

        if (modifyIndex < 0) {
            // Edit a new Quiz
            requestCode = EDIT_NEW_QUIZ_REQUEST_CODE;
        } else {
            // Edit an already existing Quiz
            requestCode = EDIT_EXISTING_QUIZ_REQUEST_CODE;
        }

        // Launch the EditQuizActivity with the extras
        Intent intent = new Intent(requireActivity(), EditQuizActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(QUIZ_BUILDER, quizBuilder);
        bundle.putSerializable(STRING_POOL, stringPool);
        intent.putExtras(bundle);
        startActivityForResult(intent, requestCode);
    }
}
