package ch.epfl.qedit.view.edit;

import static ch.epfl.qedit.view.quiz.QuestionFragment.ANSWER_FORMAT;
import static ch.epfl.qedit.view.quiz.QuestionFragment.ANSWER_MODEL;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import ch.epfl.qedit.R;
import ch.epfl.qedit.model.Question;
import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.model.answer.AnswerFormat;
import ch.epfl.qedit.model.answer.AnswerModel;
import ch.epfl.qedit.viewmodel.QuizViewModel;
import java.util.HashMap;

public class EditQuestionFragment extends Fragment {
    public static final String EDIT_ANSWER_FORMAT = "ch.epfl.qedit.view.edit.EDIT_ANSWER_FORMAT";
    public static final String EDIT_ANSWER_MODEL = "ch.epfl.qedit.view.edit.EDIT_ANSWER_MODEL";
    public static final String EDIT_FRAGMENT_TAG = "ch.epfl.qedit.view.edit.EDIT_FRAGMENT_TAG";

    private EditText editQuestionDisplay;
    private EditText editQuestionTitle;
    private QuizViewModel model;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.edit_fragment_quiz_question, container, false);
        editQuestionDisplay = v.findViewById(R.id.edit_question_display);
        editQuestionTitle = v.findViewById(R.id.edit_question_title);

        model = new ViewModelProvider(requireActivity()).get(QuizViewModel.class);

        observeModelOnEdit(model);

        return v;
    }

    private void observeModelOnEdit(final QuizViewModel model) {
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

    private boolean testOnQuiz(Quiz quiz, Integer index) {
        return index == null || quiz == null || index < 0 || index >= quiz.getQuestions().size();
    }

    /** Handles the transition from one question to another */
    private void editOnQuestionChanged(Quiz quiz, Integer index) {
        if (testOnQuiz(quiz, index)) {
            return;
        }

        Question question = quiz.getQuestions().get(index);

        // We have to change the question title and text
        String questionTitleStr = "Question " + (index + 1) + " - " + question.getTitle();
        editQuestionDisplay.setText(question.getText());
        editQuestionTitle.setText(questionTitleStr);

        prepareEditAnswerFormatFragment(question, index);
    }

    private AnswerModel getNewAnswerModel(AnswerFormat answerFormat, int index) {
        AnswerModel answerModel;
        HashMap<Integer, AnswerModel> answers = model.getAnswers().getValue();
        if (!answers.containsKey(index)) {
            answerModel = answerFormat.getNewAnswerModel();
            answers.put(index, answerModel);
            model.getAnswers().postValue(answers);
        } else {
            answerModel = answers.get(index);
        }

        return answerModel;
    }

    private void prepareEditAnswerFormatFragment(Question question, Integer index) {
        AnswerFormat answerFormat = question.getFormat();

        AnswerModel answerModel = getNewAnswerModel(answerFormat, index);

        Bundle bundle = new Bundle();
        bundle.putSerializable(ANSWER_FORMAT, answerFormat);
        bundle.putSerializable(ANSWER_MODEL, answerModel);

        Fragment editFragment = answerFormat.getNewFragment();
        editFragment.setArguments(bundle);

        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.edit_answer_fragment_container, editFragment, EDIT_FRAGMENT_TAG)
                .commit();
    }
}
