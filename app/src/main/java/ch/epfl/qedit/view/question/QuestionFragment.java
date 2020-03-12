package ch.epfl.qedit.view.question;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import ch.epfl.qedit.R;
import ch.epfl.qedit.model.Question;

public class QuestionFragment extends Fragment {

    private Question question0;
    private Question question1;
    private Question question2;

    public static QuestionFragment newInstance() {
        return new QuestionFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        question0 = (Question) getArguments().getSerializable("q0");
        question1 = (Question) getArguments().getSerializable("q1");
        question2 = (Question) getArguments().getSerializable("q2");
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.question_fragment, container, false);

        final TextView questionTitle = (TextView) view.findViewById(R.id.question_title);
        questionTitle.setText((question0.getIndex() + 1) + ") " + question0.getTitle());

        final TextView questionDisplay = (TextView) view.findViewById(R.id.question_display);
        questionDisplay.setText(question0.getText());

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
