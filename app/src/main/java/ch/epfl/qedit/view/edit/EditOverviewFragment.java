package ch.epfl.qedit.view.edit;

import static android.app.Activity.RESULT_OK;
import static ch.epfl.qedit.view.edit.EditNewQuizSettingsActivity.STRING_POOL;
import static ch.epfl.qedit.view.edit.EditQuestionActivity.QUESTION;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    public static final int EDIT_QUESTION_ACTIVITY_REQUEST_CODE = 0;

    private ListEditView.Adapter<String> adapter;
    private EditionViewModel model;
    private List<String> titles;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_overview, container, false);

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
                .setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                model.getQuizBuilder().addEmptyQuestion();
                                adapter.addItem(
                                        getResources().getString(R.string.new_empty_question));
                            }
                        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EDIT_QUESTION_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Get the question and the extended StringPool from the return data
                Question filledOutQuestion = (Question) data.getExtras().getSerializable(QUESTION);
                StringPool extendedStringPool =
                        (StringPool) data.getExtras().getSerializable(STRING_POOL);

                // Update the StringPool of the ViewModel
                model.setStringPool(extendedStringPool);

                // Update the question that was empty before by the filled out question
                int position = model.getFocusedQuestion().getValue();
                model.getQuizBuilder().update(position, filledOutQuestion);
                titles.set(position, extendedStringPool.get(filledOutQuestion.getTitle()));
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
    }

    private void createAdapter() {
        // Create an adapter for the title list
        adapter =
                new ListEditView.Adapter<String>(
                        titles,
                        new ListEditView.GetItemText<String>() {
                            @Override
                            public String getText(String item) {
                                return item;
                            }
                        });
    }

    private void setItemListener() {
        adapter.setItemListener(
                new ListEditView.ItemListener() {
                    @Override
                    public void onItemEvent(int position, ListEditView.EventType type) {
                        switch (type) {
                            case Select:
                                model.getFocusedQuestion().postValue(position);
                                break;
                            case RemoveRequest:
                                model.getFocusedQuestion().postValue(null);
                                model.getQuizBuilder().remove(position);
                                adapter.removeItem(position);
                                break;
                            case EditRequest:
                                launchEditQuestionActivity();
                                break;
                            default:
                                break;
                        }
                    }
                });
    }

    private void setMoveListener() {
        adapter.setMoveListener(
                new ListEditView.MoveListener() {
                    @Override
                    public void onItemMoved(int from, int to) {
                        model.getQuizBuilder().swap(from, to);
                    }
                });
    }

    private void launchEditQuestionActivity() {
        Intent intent = new Intent(requireActivity(), EditQuestionActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(STRING_POOL, model.getStringPool());
        intent.putExtras(bundle);
        startActivityForResult(intent, EDIT_QUESTION_ACTIVITY_REQUEST_CODE);
    }
}
