package ch.epfl.qedit.view.answer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ch.epfl.qedit.R;
import ch.epfl.qedit.model.answer.TestAnswerFormat;
import ch.epfl.qedit.model.answer.TestAnswerModel;

/** Only for test purposes */
public class TestAnswerFragment extends AnswerFragment<TestAnswerFormat, TestAnswerModel> {
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.test_answer_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
