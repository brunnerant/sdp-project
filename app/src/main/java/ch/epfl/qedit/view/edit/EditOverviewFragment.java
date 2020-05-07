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
import ch.epfl.qedit.view.util.ListEditView;
import ch.epfl.qedit.viewmodel.EditionViewModel;
import java.util.ArrayList;
import java.util.List;

/** This fragment is used to view and edit the list of questions of a quiz. */
public class EditOverviewFragment extends Fragment {
    public static final String QUESTION = "ch.epfl.qedit.view.edit.QUESTION";
    public static final int NEW_QUESTION_REQUEST_CODE = 0;
    public static final int MODIFY_QUESTION_REQUEST_CODE = 1;

    private ListEditView.Adapter<String> adapter;
    private EditionViewModel model;
    private List<String> titles;
    private TextView emptyHint;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_overview, container, false);

        emptyHint = view.findViewById(R.id.empty_list_hint);
        emptyHint.setText(getResources().getString(R.string.empty_question_list_hint_text));

        model = new ViewModelProvider(requireActivity()).get(EditionViewModel.class);

        prepareTitles();

        // Retrieve and configure the recycler view
        final ListEditView listEditView = view.findViewById(R.id.question_list);
        createAdapter();
        setItemListener();
        setMoveListener();
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
                int position = model.getQuizBuilder().size();
                model.getQuizBuilder().append(question);
                adapter.addItem(extendedStringPool.get(question.getTitle()));

                handleEmptyHint();
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

    private void prepareTitles() {
        titles = new ArrayList<>();

        // Add the titles of all question already in the builder to a list
        for (Question question : model.getQuizBuilder().getQuestions()) {
            String key = question.getTitle();
            String text = model.getStringPool().get(key);

            // TODO Support old questions that store the strings directly as well
            titles.add((text == null) ? key : text);
        }

        handleEmptyHint();
    }

    private void createAdapter() {
        // Create an adapter for the title list
        adapter = new ListEditView.Adapter<>(titles, item -> item);
    }

    private void setItemListener() {
        adapter.setItemListener(
                (position, type) -> {
                    switch (type) {
                        case Select:
                            model.getFocusedQuestion().postValue(position);
                            break;
                        case RemoveRequest:
                            model.getFocusedQuestion().postValue(null);
                            model.getQuizBuilder().remove(position);
                            adapter.removeItem(position);
                            handleEmptyHint();
                            break;
                        case EditRequest:
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

    private void setMoveListener() {
        adapter.setMoveListener((from, to) -> model.getQuizBuilder().swap(from, to));
    }

    private void handleEmptyHint() {
        if (titles.size() == 0) {
            emptyHint.setVisibility(VISIBLE);
        } else {
            emptyHint.setVisibility(GONE);
        }
    }

    private void launchEditQuestionActivity(Question question) {
        Intent intent = new Intent(requireActivity(), EditQuestionActivity.class);
        Bundle bundle = new Bundle();

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
}
