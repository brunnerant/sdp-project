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
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import ch.epfl.qedit.R;
import ch.epfl.qedit.backend.database.DatabaseFactory;
import ch.epfl.qedit.backend.database.DatabaseService;
import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.model.User;
import ch.epfl.qedit.util.Callback;
import ch.epfl.qedit.util.Response;
import ch.epfl.qedit.view.quiz.QuizActivity;
import ch.epfl.qedit.view.util.ConfirmDialogFragment;
import ch.epfl.qedit.view.util.ListEditView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class HomeQuizListFragment extends Fragment implements ConfirmDialogFragment.ConfirmationListener {
    public static final String QUIZID = "ch.epfl.qedit.view.QUIZID";

    private DatabaseService db;
    private Handler handler;

    private ProgressBar progressBar;
    private ListEditView.ListEditAdapter<Map.Entry<String, String>> listAdapter;

    private ConfirmDialogFragment deleteDialog;
    private int deleteIndex;

    private User user;
    private List<Map.Entry<String, String>> quizzes;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_home_quiz_list, container, false);

        // Get user from the bundle created by the parent activity
        user = (User) Objects.requireNonNull(getArguments()).getSerializable(USER);
        quizzes = new ArrayList<>(user.getQuizzes().entrySet().asList());

        // Create the list adapter and bind it to the list edit view
        createAdapter(user);
        ListEditView listEditView = view.findViewById(R.id.home_quiz_list);
        listEditView.setAdapter(listAdapter);

        // The progress bar is needed while waiting from the database
        progressBar = view.findViewById(R.id.quiz_loading);

        // This is used to confirm that the user actually wants to delete a quiz
        deleteDialog = new ConfirmDialogFragment(getString(R.string.warning_delete), this);

        // Instantiate Handler and the DatabaseService
        db = DatabaseFactory.getInstance();
        handler = new Handler();

        return view;
    }

    private void createAdapter(User user) {
        // Retrieve the quizzes from the user
        quizzes = new ArrayList<>(user.getQuizzes().entrySet().asList());

        // Create the list adapter
        listAdapter = new ListEditView.ListEditAdapter<>(quizzes, new ListEditView.GetItemText<Map.Entry<String, String>>() {
            @Override
            public String getText(Map.Entry<String, String> item) {
                return item.getValue();
            }
        });

        // Listen to the data changes
        listAdapter.setItemListener(new ListEditView.ItemListener() {
            @Override
            public void onItemEvent(int position, ListEditView.EventType type) {
                switch (type) {
                    case RemoveRequest:
                        deleteQuiz(position);
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
        inflater.inflate(R.menu.menu_editor_mode, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                // TODO add edit popup
                break;
            case android.R.id.home:
                requireActivity().onBackPressed();
                break;
        }

        return true;
    }

    // Handles when a user clicked on the button to remove a quiz
    private void deleteQuiz(int position) {
        deleteDialog.show(getParentFragmentManager(), "delete_confirmation");
        deleteIndex = position;
    }

    // Handles when a user clicked on the button to edit a quiz
    private void editQuiz(int position) {
        String quizID = quizzes.get(position).getKey();
        progressBar.setVisibility(View.VISIBLE);

        // Query quiz questions from the database
        db.getQuiz(
                quizID,
                new Callback<Response<Quiz>>() {
                    @Override
                    public void onReceive(final Response<Quiz> response) {
                        handler.post(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        // Determine what to do when the quiz is loaded or not
                                        progressBar.setVisibility(View.GONE);
                                        if (response.getError().noError(getContext()))
                                            launchQuizActivity(response.getData());
                                    }
                                });
                    }
                });
    }

    // Launches the quiz activity with the given quiz. This is used when a quiz is selected.
    private void launchQuizActivity(Quiz quiz) {
        Intent intent = new Intent(requireActivity(), QuizActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(QUIZID, quiz);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onConfirm(ConfirmDialogFragment dialog) {
        if (dialog != deleteDialog)
            return;

        listAdapter.removeItem(deleteIndex);
    }
}
