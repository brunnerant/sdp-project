package ch.epfl.qedit.view.answer;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import ch.epfl.qedit.model.answer.AnswerFormat;
import ch.epfl.qedit.model.answer.AnswerModel;

import static ch.epfl.qedit.view.quiz.QuestionFragment.ANSWER_FORMAT;
import static ch.epfl.qedit.view.quiz.QuestionFragment.ANSWER_MODEL;

public class AnswerFragment<F extends AnswerFormat, M extends AnswerModel> extends Fragment {
    protected F answerFormat;
    protected M answerModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        answerFormat = (F) requireArguments().getSerializable(ANSWER_FORMAT);
        answerModel = (M) requireArguments().getSerializable(ANSWER_MODEL);
    }
}
