package ch.epfl.qedit.view.edit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import ch.epfl.qedit.R;
import ch.epfl.qedit.model.Question;
import ch.epfl.qedit.model.answer.AnswerFormat;
import ch.epfl.qedit.viewmodel.EditionViewModel;

public class EditQuestionFragment extends Fragment {
    public static final String EDIT_ANSWER_FORMAT = "ch.epfl.qedit.view.edit.EDIT_ANSWER_FORMAT";
    public static final String EDIT_ANSWER_MODEL = "ch.epfl.qedit.view.edit.EDIT_ANSWER_MODEL";
    public static final String EDIT_FRAGMENT_TAG = "ch.epfl.qedit.view.edit.EDIT_FRAGMENT_TAG";

    private TextView questionTitle;
    private TextView questionDisplay;
    private EditionViewModel model;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_quiz_question, container, false);
        questionTitle = view.findViewById(R.id.question_title);
        questionDisplay = view.findViewById(R.id.question_display);

        model = new ViewModelProvider(requireActivity()).get(EditionViewModel.class);

        model.getFocusedQuestion()
                .observe(
                        getViewLifecycleOwner(),
                        new Observer<Integer>() {
                            @Override
                            public void onChanged(Integer index) {
                                onQuestionChanged(index);
                            }
                        });

        return view;
    }

    /** Handles the transition from one question to another */
    private void onQuestionChanged(Integer index) {
        if (index == null || index < 0 || index >= model.getOverviewList().size()) {
            questionTitle.setText("");
            questionDisplay.setText("");
            return;
        }

        Question question = model.getOverviewList().get(index);

        if (question == null) {
            questionTitle.setText("Title");
            questionDisplay.setText("Text");
        } else {
            // We have to change the question title and text
            String questionTitleStr = "Question " + (index + 1) + " - " + question.getTitle();
            questionTitle.setText(questionTitleStr);
            questionDisplay.setText(question.getText());

            // Set everything up for the concrete AnswerFragment and launch it
            // prepareAnswerFormatFragment(question, index); //TODO
        }
    }

    /**
     * This method gets the concrete AnswerFormat, checks if the QuizViewModel contains already a
     * matching AnswerModel and otherwise creates a new one and adds it to the QuizViewModel.
     * Further a bundle is prepared, then it dispatches the correct Fragment class and finally
     * starts it.
     *
     * @param question The question that is going to be shown
     * @param index The index of that Question in the Quiz, the question list
     */
    private void prepareAnswerFormatFragment(Question question, Integer index) {
        // Get the AnswerFormat of the question
        AnswerFormat answerFormat = question.getFormat();

        //        AnswerModel answerModel; TODO
        //        HashMap<Integer, AnswerModel> answers = quizViewModel.getAnswers().getValue();
        //
        //        // Check if the model already holds an AnswerModel for this question
        //        if (answers.containsKey(index)) {
        //            answerModel = answers.get(index);
        //        } else { // and otherwise create a new one and add it to the QuizViewModel
        //            answerModel = answerFormat.getEmptyAnswerModel();
        //            answers.put(index, answerModel);
        //            quizViewModel.getAnswers().postValue(answers);
        //        }

        // Prepare the bundle for the Fragment
        Bundle bundle = new Bundle();
        bundle.putSerializable(EDIT_ANSWER_FORMAT, answerFormat);
        // bundle.putSerializable(EDIT_ANSWER_MODEL, answerModel);

        // Get the fragment that matches the concrete type of AnswerFormat
        Fragment fragment = answerFormat.getAnswerFragment();
        fragment.setArguments(bundle);

        // And dynamically instantiate the answer form
        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.answer_fragment_container, fragment, EDIT_FRAGMENT_TAG)
                .commit();
    }
}
