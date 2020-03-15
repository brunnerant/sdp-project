package ch.epfl.qedit.view.question;

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
import ch.epfl.qedit.viewmodel.QuizViewModel;

public class QuestionFragment extends Fragment {
    private TextView questionTitle;
    private TextView questionDisplay;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.question_fragment, container, false);
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
                                onQuestionChanged(model.getQuiz().getValue(), index);
                            }
                        });

        model.getStatus()
                .observe(
                        getViewLifecycleOwner(),
                        new Observer<QuizViewModel.Status>() {
                            @Override
                            public void onChanged(QuizViewModel.Status status) {
                                onQuestionChanged(
                                        model.getQuiz().getValue(),
                                        model.getFocusedQuestion().getValue());
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
        String questionTitleStr = "Question " + (index + 1) + ": " + question.getTitle();
        questionTitle.setText(questionTitleStr);
        questionDisplay.setText(question.getText());

        // And dynamically instatiate the answer form
        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.answer_fragment_container, new DummyAnswerFragment())
                .commit();
    }
}
