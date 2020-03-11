package ch.epfl.qedit.view.question;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Objects;

import ch.epfl.qedit.R;
import ch.epfl.qedit.model.Question;

public class QuestionFragment extends Fragment {

    private Question question;

    public static QuestionFragment newInstance() {
        return new QuestionFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        question = (Question) Objects.requireNonNull(getArguments()).getSerializable("q0");
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.question_fragment, container, false);

        final TextView questionTitle = view.findViewById(R.id.question_title);
        String questionTitleStr = (question.getIndex() + 1) + ") " + question.getTitle();
        questionTitle.setText(questionTitleStr);

        final TextView questionDisplay = view.findViewById(R.id.question_display);
        questionDisplay.setText(question.getText());

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
