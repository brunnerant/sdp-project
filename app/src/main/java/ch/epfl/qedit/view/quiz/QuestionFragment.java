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
import ch.epfl.qedit.model.AnswerFormat;
import ch.epfl.qedit.model.Question;
import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.viewmodel.QuizViewModel;

public class QuestionFragment extends Fragment {
    public static final String ANSWER_FORMAT = "ch.epfl.qedit.view.ANSWER_FORMAT";
    public static final String ANSWER_MODEL = "ch.epfl.qedit.view.ANSWER_MODEL";

    private TextView questionTitle;
    private TextView questionDisplay;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_quiz_question, container, false);
        questionTitle = view.findViewById(R.id.question_title);
        questionDisplay = view.findViewById(R.id.question_display);

        final QuizViewModel model =
                new ViewModelProvider(requireActivity()).get(QuizViewModel.class);

        model.getFocusedQuestion()
                .observe(
                        getViewLifecycleOwner(),
                        new Observer<Integer>() {
                            @Override
                            public void onChanged(Integer index) {
                                onQuestionChanged(model.getQuiz(), index);
                            }
                        });

        return view;
    }

    /** Handles the transition from one question to another */
    private void onQuestionChanged(Quiz quiz, Integer index) {
        if (index == null || quiz == null || index < 0 || index >= quiz.getQuestions().size())
            return;

        Question question = quiz.getQuestions().get(index);

        // We have to change the question title and text
        String questionTitleStr = "Question " + (index + 1) + " - " + question.getTitle();
        questionTitle.setText(questionTitleStr);
        questionDisplay.setText(question.getText());

        AnswerFormat answerFormat = quiz.getQuestions().get(index).getFormat();

        Bundle bundle = new Bundle();
        bundle.putSerializable(ANSWER_FORMAT, answerFormat);
        bundle.putSerializable(ANSWER_MODEL, answerFormat.emptyAnswerModel());

        Fragment fragment = answerFormat.getFragment();
        fragment.setArguments(bundle);

        // And dynamically instantiate the answer form
        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.answer_fragment_container, fragment)
                .commit();
    }
}
