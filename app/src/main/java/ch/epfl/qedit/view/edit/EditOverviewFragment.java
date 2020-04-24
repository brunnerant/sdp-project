package ch.epfl.qedit.view.edit;

import static android.app.Activity.RESULT_OK;
import static ch.epfl.qedit.view.edit.EditNewQuizSettingsActivity.QUIZ_BUILDER;
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
import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.model.StringPool;
import ch.epfl.qedit.view.quiz.QuestionFragment;
import ch.epfl.qedit.view.util.ListEditView;
import ch.epfl.qedit.viewmodel.EditionViewModel;

/** This fragment is used to view and edit the list of questions of a quiz. */
public class EditOverviewFragment extends Fragment {
    public static final int EDIT_QUESTION_ACTIVITY_REQUEST_CODE = 0;
    public static final String QUESTION_BUILDER = "ch.epfl.qedit.view.edit.QUESTION_BUILDER";

    private ListEditView.Adapter<Question> adapter;
    private EditionViewModel model;
    private Quiz.Builder quizBuilder;
    private StringPool stringPool;

    // TODO title in top bar

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_overview, container, false);

        quizBuilder = (Quiz.Builder) getArguments().getSerializable(QUIZ_BUILDER);
        stringPool = (StringPool) getArguments().getSerializable(STRING_POOL);

        model = new ViewModelProvider(requireActivity()).get(EditionViewModel.class);

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
                                adapter.addItem(null);
                            }
                        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EDIT_QUESTION_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Question filledOutQuestion = (Question) data.getExtras().getSerializable(QUESTION);
                int position = model.getFocusedQuestion().getValue();
                quizBuilder.add(position, filledOutQuestion);
                model.addFilledOutQuestion(position, filledOutQuestion);
                // TODO update adapter item, how?
            }
        }
    }

    private void createAdapter() {
        // Create an adapter for the question list
        adapter =
                new ListEditView.Adapter<>(
                        model.getOverviewList(),
                        new ListEditView.GetItemText<Question>() {
                            @Override
                            public String getText(Question item) {
                                if (item == null) {
                                    return "New Question"; // TODO relative string
                                }

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
                                model.getFocusedQuestion().postValue(position);
                                break;
                            case RemoveRequest:
                                model.getFocusedQuestion().postValue(null);
                                if (model.getOverviewList().get(position) != null) {
                                    quizBuilder.remove(position);
                                }
                                adapter.removeItem(position);
                                break;
                            case EditRequest:
                                // launch EditQuestion activity
                                Intent intent =
                                        new Intent(requireActivity(), EditQuestionActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable(QUESTION_BUILDER, new Question.Builder()); //TODO if the question is not empty initialize builder
                                bundle.putSerializable(STRING_POOL, stringPool);
                                intent.putExtras(bundle);
                                startActivityForResult(intent, EDIT_QUESTION_ACTIVITY_REQUEST_CODE);
                                break;
                            default:
                                break;
                        }
                    }
                });
    }

    private void setFragment(Fragment fragment) {
        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.question_details_container, fragment)
                .commit();
    }
}
