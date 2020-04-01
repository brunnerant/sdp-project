package ch.epfl.qedit.view.edit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import ch.epfl.qedit.model.MatrixFormat;
import ch.epfl.qedit.model.Question;
import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.R;
import ch.epfl.qedit.view.answer.MatrixFragment;
import ch.epfl.qedit.viewmodel.QuizViewModel;

public class EditQuestionFragment extends Fragment {
    private EditText editQuestionDisplay;
    private EditText editQuestionTitle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.edit_fragment_quiz_question, container, false);
        editQuestionDisplay = v.findViewById(R.id.edit_question_display);
        editQuestionTitle = v.findViewById(R.id.edit_question_title);

        final QuizViewModel model =
                new ViewModelProvider(requireActivity()).get(QuizViewModel.class);

        observe(model);

//        model.getFocusedQuestion()
//                .observe(
//                        getViewLifecycleOwner(),
//                        new Observer<Integer>() {
//                            @Override
//                            public void onChanged(Integer index) {
//                                editOnQuestionChanged(model.getQuiz(), index);
//                            }
//                        });

        return v;
    }

    private void observe(final QuizViewModel model) {
        model.getFocusedQuestion()
                .observe(
                        getViewLifecycleOwner(),
                        new Observer<Integer>() {
                            @Override
                            public void onChanged(Integer index) {
                                editOnQuestionChanged(model.getQuiz(), index);
                            }
                        });
    }

    /** Handles the transition from one question to another */
    private void editOnQuestionChanged(Quiz quiz, Integer index) {
        Question question = quiz.getQuestions().get(index);

        // We have to change the question title and text
        String questionTitleStr = "Question " + (index + 1) + " - " + question.getTitle();
        editQuestionDisplay.setText(question.getText());
        editQuestionTitle.setText(questionTitleStr);

        MatrixFragment matrixFragment = new MatrixFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(MatrixFragment.MATRIXID, question.getFormat());
        matrixFragment.setArguments(bundle);

        // And dynamically instantiate the answer form
        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.edit_answer_fragment_container, matrixFragment)
                .commit();
    }
}
