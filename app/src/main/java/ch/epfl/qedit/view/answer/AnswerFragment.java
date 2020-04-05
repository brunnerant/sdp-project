package ch.epfl.qedit.view.answer;

import static ch.epfl.qedit.view.quiz.QuestionFragment.ANSWER_FORMAT;
import static ch.epfl.qedit.view.quiz.QuestionFragment.ANSWER_MODEL;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import ch.epfl.qedit.model.answer.AnswerFormat;
import ch.epfl.qedit.model.answer.AnswerModel;

/**
 * This is a helper class for all the answer fragments. It allows to retrieve automatically the
 * answer format and answer model that were passed by the question fragment.
 *
 * @param <F> the type of the answer format
 * @param <M> the type of the answer model
 */
public class AnswerFragment<F extends AnswerFormat, M extends AnswerModel> extends Fragment {
    /**
     * Those fields can be retrieved in the subclasses so that they don't need to redundantly get
     * them from the arguments
     */
    F answerFormat;

    M answerModel;

    @Override
    @SuppressWarnings("unchecked")
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        answerFormat = (F) requireArguments().getSerializable(ANSWER_FORMAT);
        answerModel = (M) requireArguments().getSerializable(ANSWER_MODEL);
    }
}
