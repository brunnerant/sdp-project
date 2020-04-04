package ch.epfl.qedit.view.quiz;

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
import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.model.answer.AnswerFormat;
import ch.epfl.qedit.model.answer.AnswerModel;
import ch.epfl.qedit.viewmodel.QuizViewModel;
import java.util.HashMap;

public class QuestionFragment extends Fragment {
    public static final String ANSWER_FORMAT = "ch.epfl.qedit.view.ANSWER_FORMAT";
    public static final String ANSWER_MODEL = "ch.epfl.qedit.view.ANSWER_MODEL";
    public static final String FRAGMENT_TAG = "ch.epfl.qedit.view.FRAGMENT_TAG";

    private TextView questionTitle;
    private TextView questionDisplay;
    private QuizViewModel quizViewModel;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_quiz_question, container, false);
        questionTitle = view.findViewById(R.id.question_title);
        questionDisplay = view.findViewById(R.id.question_display);

        quizViewModel = new ViewModelProvider(requireActivity()).get(QuizViewModel.class);

        quizViewModel
                .getFocusedQuestion()
                .observe(
                        getViewLifecycleOwner(),
                        new Observer<Integer>() {
                            @Override
                            public void onChanged(Integer index) {
                                onQuestionChanged(quizViewModel.getQuiz(), index);
                            }
                        });

        return view;
    }

    /** Handles the transition from one question to another */
    private void onQuestionChanged(Quiz quiz, Integer index) {
        if (index == null || quiz == null || index < 0 || index >= quiz.getQuestions().size()) {
            return;
        }

        Question question = quiz.getQuestions().get(index);

        // We have to change the question title and text
        String questionTitleStr = "Question " + (index + 1) + " - " + question.getTitle();
        questionTitle.setText(questionTitleStr);
        questionDisplay.setText(question.getText());

        // Set everything up for the concrete AnswerFragment and launch it
        prepareAnswerFormatFragment(question, index);
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

        AnswerModel answerModel;
        HashMap<Integer, AnswerModel> answers = quizViewModel.getAnswers().getValue();

        // Check if the model already holds an AnswerModel for this question
        if (answers.containsKey(index)) {
            answerModel = answers.get(index);
        } else { // and otherwise create a new one and add it to the QuizViewModel
            answerModel = answerFormat.getNewAnswerModel();
            answers.put(index, answerModel);
            quizViewModel.getAnswers().postValue(answers);
        }

        // Prepare the bundle for the Fragment
        Bundle bundle = new Bundle();
        bundle.putSerializable(ANSWER_FORMAT, answerFormat);
        bundle.putSerializable(ANSWER_MODEL, answerModel);

        // Get the fragment that matches the concrete type of AnswerFormat
        Fragment fragment = answerFormat.getNewFragment();
        fragment.setArguments(bundle);

        // And dynamically instantiate the answer form
        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.answer_fragment_container, fragment, FRAGMENT_TAG)
                .commit();
    }
}
