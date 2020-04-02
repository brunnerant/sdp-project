package ch.epfl.qedit.view.edit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import ch.epfl.qedit.R;
import ch.epfl.qedit.model.Question;
import ch.epfl.qedit.model.answer.MatrixFormat;
import ch.epfl.qedit.view.quiz.QuestionFragment;
import ch.epfl.qedit.view.util.ListEditView;
import ch.epfl.qedit.viewmodel.QuizViewModel;
import java.util.LinkedList;
import java.util.List;

/** This fragment is used to view and edit the list of questions of a quiz. */
public class EditOverviewFragment extends Fragment {
    private List<Question> questions;
    private int numQuestions;
    private ListEditView.Adapter<Question> adapter;
    private QuizViewModel model;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_overview, container, false);
        model = new ViewModelProvider(requireActivity()).get(QuizViewModel.class);
        // Retrieve and configure the recycler view
        ListEditView listEditView = view.findViewById(R.id.question_list);
        createAdapter();
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
                                                new MatrixFormat(1, 1)));
                            }
                        });

        return view;
    }

    private void createAdapter() {
        addDummyQuestions();

        // Create an adapter for the question list
        adapter =
                new ListEditView.Adapter<>(
                        questions,
                        new ListEditView.GetItemText<Question>() {
                            @Override
                            public String getText(Question item) {
                                return item.getTitle();
                            }
                        });

        adapter.setItemListener(
                new ListEditView.ItemListener() {
                    @Override
                    public void onItemEvent(int position, ListEditView.EventType type) {
                        switch (type) {
                            case Select:
                                getActivity()
                                        .getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(
                                                R.id.question_details_container,
                                                new QuestionFragment())
                                        .commit();
                                model.getFocusedQuestion().postValue(position);
                                break;
                            case RemoveRequest:
                                adapter.removeItem(position);
                                break;
                            case EditRequest:
                                getActivity()
                                        .getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(
                                                R.id.question_details_container,
                                                new EditQuestionFragment())
                                        .commit();
                                break;
                            default:
                                break;
                        }
                    }
                });
    }

    private void addDummyQuestions() {
        // For now, we just add dummy questions to the quiz
        questions = new LinkedList<>();
        for (numQuestions = 0; numQuestions < 5; numQuestions++)
            questions.add(
                    new Question(
                            "Q" + (numQuestions + 1),
                            "is it " + (numQuestions + 1) + "?",
                            new MatrixFormat(1, 1)));
    }
}
