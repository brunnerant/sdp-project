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
    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.question_fragment, container, false);

        final TextView questionTitle = view.findViewById(R.id.question_title);
        final TextView questionDisplay = view.findViewById(R.id.question_display);
        final QuizViewModel model =
                new ViewModelProvider(requireActivity()).get(QuizViewModel.class);

        model.getFocusedQuestion()
                .observe(
                        getViewLifecycleOwner(),
                        new Observer<Integer>() {
                            @Override
                            public void onChanged(Integer index) {
                                Quiz quiz = model.getQuiz().getValue();

                                if (index == null || quiz == null) return;

                                Question question = quiz.getQuestions().get(index);

                                String questionTitleStr = (index + 1) + ") " + question.getTitle();
                                questionTitle.setText(questionTitleStr);
                                questionDisplay.setText(question.getText());

                                DummyAnswerFragment answerFragment = new DummyAnswerFragment();

                                requireActivity()
                                        .getSupportFragmentManager()
                                        .beginTransaction()
                                        .add(
                                                R.id.answer_fragment_container,
                                                new DummyAnswerFragment())
                                        .commit();
                            }
                        });

        return view;
    }
}
