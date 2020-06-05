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
import androidx.fragment.app.FragmentTransaction;
import ch.epfl.qedit.R;
import ch.epfl.qedit.backend.database.DatabaseFactory;
import ch.epfl.qedit.backend.database.DatabaseService;
import ch.epfl.qedit.backend.database.Util;
import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.model.StringPool;
import ch.epfl.qedit.model.User;
import ch.epfl.qedit.view.QR.QRGeneratorFragment;
import ch.epfl.qedit.view.edit.EditQuizActivity;
import ch.epfl.qedit.view.edit.EditQuizSettingsDialog;
import ch.epfl.qedit.view.quiz.QuizActivity;
import ch.epfl.qedit.view.treasurehunt.TreasureHuntActivity;
import ch.epfl.qedit.view.util.ConfirmDialog;
import ch.epfl.qedit.view.util.ListEditView;
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
    private static final int EDIT_QUIZ_REQUEST_CODE = 2;

    private DatabaseService db;

    private ProgressBar progressBar;
    private ListEditView.Adapter<Map.Entry<String, String>> listAdapter;

    private ConfirmDialog deleteDialog;
    private EditQuizSettingsDialog.TextFilter textFilter = NO_FILTER;

    private int deleteIndex;
    private int modifyIndex = -1;

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
        User user = (User) Objects.requireNonNull(getArguments()).getSerializable(USER);
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

                    return null;
                };
    }

    // This function is used to create the list of quizzes for the given user
    private void createAdapter(User user) {
        // Retrieve the quizzes from the user
        quizzes = new ArrayList<>(user.getQuizzes().entrySet().asList());

        // Those are the items of the popup menu
        List<String> popupMenuItems =
                Arrays.asList(
                        getString(R.string.menu_edit),
                        getString(R.string.menu_delete),
                        getString(R.string.menu_qr));

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
                    else if (code == 2) // QR Code was clicked
                    generateQR(position);
                });
    }

    private void generateQR(int position) {
        FragmentTransaction ft = requireActivity().getSupportFragmentManager().beginTransaction();

        QRGeneratorFragment fragment = new QRGeneratorFragment();

        Bundle bundle = new Bundle();
        bundle.putString(QUIZ_ID, quizzes.get(position).getKey());
        fragment.setArguments(bundle);

        ft.replace(R.id.home_qr_container, fragment);
        ft.commit();
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
                                                    if (throwable != null) {
                                                        Toast.makeText(
                                                                        requireContext(),
                                                                        R.string.database_error,
                                                                        Toast.LENGTH_SHORT)
                                                                .show();
                                                    } else {
                                                        // Hide progress bar
                                                        progressBar.setVisibility(GONE);
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
        if (requestCode == EDIT_QUIZ_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Get the quiz and the extended StringPool from the returned data
                Quiz quiz = (Quiz) data.getExtras().getSerializable(QUIZ_ID);
                StringPool extendedStringPool =
                        (StringPool) data.getExtras().getSerializable(STRING_POOL);
                // TODO send back to data base etc.
            }
            // When the user decides to stop the edition without saving the changes the
            // EditQuizActivity will return with RESULT_CANCELED. But we do not need to do anything
            // in this case.
        }
    }

    // This method will be called when the user confirms the deletion by clicking on "yes"
    @Override
    public void onConfirm(ConfirmDialog dialog) {
        if (dialog != deleteDialog) return;

        listAdapter.removeItem(deleteIndex);
    }

    // This method will be called when the user submits the settings made in the SettingsDialog by
    // clicking on "Start editing"
    @Override
    public void onSubmit(StringPool stringPool, Quiz.Builder quizBuilder) {
        String title = stringPool.get(TITLE_ID);

        if (modifyIndex < 0) {
            // Edit a new Quiz, add an new entry in the list of quizzes
            listAdapter.addItem(new AbstractMap.SimpleEntry<>("key", title));
        } else {
            // Edit an already existing Quiz, so we have to update the existing entry in the list of
            // quizzes
            Map.Entry<String, String> oldEntry = quizzes.get(modifyIndex);
            AbstractMap.SimpleImmutableEntry<String, String> newEntry =
                    new AbstractMap.SimpleImmutableEntry<>(oldEntry.getKey(), title);
            quizzes.set(modifyIndex, newEntry);
            listAdapter.updateItem(modifyIndex);
            modifyIndex = -1;
        }

        // Launch the EditQuizActivity with the extras
        Intent intent = new Intent(requireActivity(), EditQuizActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(QUIZ_BUILDER, quizBuilder);
        bundle.putSerializable(STRING_POOL, stringPool);
        intent.putExtras(bundle);
        startActivityForResult(intent, EDIT_QUIZ_REQUEST_CODE);
    }
}
