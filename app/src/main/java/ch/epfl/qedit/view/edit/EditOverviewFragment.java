package ch.epfl.qedit.view.edit;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import ch.epfl.qedit.R;
import ch.epfl.qedit.model.Question;
import ch.epfl.qedit.model.answer.MatrixFormat;
import ch.epfl.qedit.view.quiz.QuestionFragment;
import ch.epfl.qedit.view.util.ListEditView;
import ch.epfl.qedit.viewmodel.EditionViewModel;

/** This fragment is used to view and edit the list of questions of a quiz. */
public class EditOverviewFragment extends Fragment {
    private int numQuestions;

    private ListEditView.Adapter<Question> adapter;
    private EditionViewModel model;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_overview, container, false);

        Activity activity = requireActivity();
        Activity parent = activity.getParent();
        // TODO getParent won't work unless this activity is embedded as a child
        model =
                new ViewModelProvider((ViewModelStoreOwner) requireActivity().getParent())
                        .get(EditionViewModel.class);

        numQuestions = model.getQuizBuilder().numberOfQuestions();

        // Retrieve and configure the recycler view
        ListEditView listEditView = view.findViewById(R.id.question_list);
        createAdapter();
        setListener();
        listEditView.setAdapter(adapter);

        // Configure the add button
        view.findViewById(R.id.add_question_button)
                .setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                numQuestions++;
                                adapter.addItem(
                                        new Question(
                                                "Q" + numQuestions,
                                                "is it " + numQuestions + "?",
                                                MatrixFormat.singleField(MatrixFormat.Field.textField("", 25))));
                            }
                        });

        return view;
    }

    private void createAdapter() {
        // Create an adapter for the question list
        adapter =
                new ListEditView.Adapter<>(
                        model.getQuizBuilder().getQuestions(),
                        new ListEditView.GetItemText<Question>() {
                            @Override
                            public String getText(Question item) {
                                return item.getTitle();
                            }
                        });
    }

    private void setListener() {
        adapter.setItemListener(
                new ListEditView.ItemListener() {
                    @Override
                    public void onItemEvent(int position, ListEditView.EventType type) {
                        switch (type) {
                            case Select:
                                setFragment(new QuestionFragment());
                                model.getFocusedQuestion().postValue(position);
                                break;
                            case RemoveRequest:
                                adapter.removeItem(position);
                                model.getQuizBuilder().remove(position);
                                updateFocus(position);
                                break;
                            case EditRequest:
                                setFragment(new EditQuestionFragment());
                                break;
                            default:
                                break;
                        }
                    }
                });
    }

    private void updateFocus(int position) {
        if (model.getQuizBuilder().numberOfQuestions() == 0) {
            model.getFocusedQuestion().postValue(null);
        } else if (position <= model.getFocusedQuestion().getValue()) {
            // TODO stuff
        }
        model.getFocusedQuestion();
    }

    private void setFragment(Fragment fragment) {
        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.question_details_container, fragment)
                .commit();
    }
}

