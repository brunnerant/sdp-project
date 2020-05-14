package ch.epfl.qedit.view.edit;

import static android.app.Activity.RESULT_OK;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static ch.epfl.qedit.view.home.HomeQuizListFragment.STRING_POOL;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import ch.epfl.qedit.R;
import ch.epfl.qedit.model.Question;
import ch.epfl.qedit.model.StringPool;
import ch.epfl.qedit.view.util.ConfirmDialog;
import ch.epfl.qedit.view.util.ListEditView;
import ch.epfl.qedit.viewmodel.EditionViewModel;
import java.util.ArrayList;
import java.util.List;

/** This fragment is used to view and edit the list of questions of a quiz. */
public class EditOverviewFragment extends Fragment implements ConfirmDialog.ConfirmationListener {
    public static final String QUESTION = "ch.epfl.qedit.view.edit.QUESTION";
    public static final int NEW_QUESTION_REQUEST_CODE = 0;
    public static final int MODIFY_QUESTION_REQUEST_CODE = 1;

    private ConfirmDialog deleteQuestionDialog;
    private int deleteIndex;

    private ListEditView.Adapter<String> adapter;
    private EditionViewModel model;
    private List<String> titles;
    private TextView emptyHint;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_overview, container, false);

        // Setup the hint that tells the user to add questions
        emptyHint = view.findViewById(R.id.empty_list_hint);
        emptyHint.setText(getResources().getString(R.string.empty_question_list_hint_text));

        // Initialize the ViewModel, the list for the ListEditView and the ConfirmationDialog that
        // asks the user if he really wants to delete the question
        model = new ViewModelProvider(requireActivity()).get(EditionViewModel.class);
        prepareTitles();
        deleteQuestionDialog =
                ConfirmDialog.create(getString(R.string.warning_delete_question), this);

        // Retrieve and configure the recycler view
        final ListEditView listEditView = view.findViewById(R.id.question_list);
        adapter = new ListEditView.Adapter<>(titles, item -> item);
        adapter.setMoveListener((from, to) -> model.getQuizBuilder().swap(from, to));
        setItemListener();
        listEditView.setAdapter(adapter);

        // Configure the add button
        view.findViewById(R.id.add_question_button)
                .setOnClickListener(v -> launchEditQuestionActivity(null));

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            // Get the question and the extended StringPool from the returned data
            Question question = (Question) data.getExtras().getSerializable(QUESTION);
            StringPool extendedStringPool =
                    (StringPool) data.getExtras().getSerializable(STRING_POOL);

            // Update the StringPool of the ViewModel
            model.setStringPool(extendedStringPool);

            if (requestCode == NEW_QUESTION_REQUEST_CODE) {
                // This is a new question, so append it at the end and remove the empty hint if
                // needed
                int position = model.getQuizBuilder().size();
                model.getQuizBuilder().append(question);
                adapter.addItem(extendedStringPool.get(question.getTitle()));

                handleEmptyHint();

                // Open the new question in the preview
                model.getFocusedQuestion().postValue(position);
            } else if (requestCode == MODIFY_QUESTION_REQUEST_CODE) {
                // Update the already existing question
                int position = model.getFocusedQuestion().getValue();
                model.getQuizBuilder().update(position, question);
                titles.set(position, extendedStringPool.get(question.getTitle()));
                adapter.updateItem(position);

                // Trigger the preview fragment to draw the updated title and text
                model.getFocusedQuestion().postValue(position);
            }
        }
    }

    /**
     * Gets the titles from the questions if we're editing an existing quiz and prepares a list for
     * the EditListView
     */
    private void prepareTitles() {
        titles = new ArrayList<>();

        // Add the titles of all question already in the builder to a list
        for (Question question : model.getQuizBuilder().getQuestions()) {
            titles.add(model.getStringPool().get(question.getTitle()));
        }

        handleEmptyHint();
    }

    /** Fix the behavior the EditListView when the user interacts with it */
    private void setItemListener() {
        adapter.setItemListener(
                (position, type) -> {
                    switch (type) {
                        case Select:
                            model.getFocusedQuestion().postValue(position);
                            break;
                        case RemoveRequest:
                            // Open the ConfirmDialog that asks if the user really wants to remove
                            // the question
                            deleteIndex = position;
                            deleteQuestionDialog.show(getParentFragmentManager(), "delete_dialog");
                            break;
                        case EditRequest:
                            // Edit an existing question
                            launchEditQuestionActivity(
                                    model.getQuizBuilder()
                                            .getQuestions()
                                            .get(model.getFocusedQuestion().getValue()));
                            break;
                        default:
                            break;
                    }
                });
    }

    /** Handle the empty list of questions hint, its only shown when the list is actually empty */
    private void handleEmptyHint() {
        emptyHint.setVisibility(titles.size() == 0 ? VISIBLE : GONE);
    }

    /** Prepares the bundle for the EditQuestionActivity and launches it */
    private void launchEditQuestionActivity(Question question) {
        Intent intent = new Intent(requireActivity(), EditQuestionActivity.class);
        Bundle bundle = new Bundle();

        // Set the request code according to if the question is empty or not
        int requestCode;
        if (question == null) {
            requestCode = NEW_QUESTION_REQUEST_CODE;
        } else {
            requestCode = MODIFY_QUESTION_REQUEST_CODE;
            bundle.putSerializable(QUESTION, question);
        }

        bundle.putSerializable(STRING_POOL, model.getStringPool());
        intent.putExtras(bundle);
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onConfirm(ConfirmDialog dialog) {
        // Deselect the question and remove it
        model.getFocusedQuestion().postValue(null);
        model.getQuizBuilder().remove(deleteIndex);
        adapter.removeItem(deleteIndex);
        handleEmptyHint();
    }
}
