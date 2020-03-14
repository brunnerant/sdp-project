package ch.epfl.qedit.view.question;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import ch.epfl.qedit.R;
import ch.epfl.qedit.model.AnswerFormat;
import ch.epfl.qedit.model.Question;
import java.util.Objects;

public class QuestionFragment extends Fragment {
    private Question question;
    private AnswerFormat answerFormat;
    private int layout;

    public QuestionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        question = (Question) Objects.requireNonNull(getArguments()).getSerializable("question");

        // Get the actual type of the AnswerFormat
        Visitor visitor = new Visitor();
        question.getFormat().accept(visitor);
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

        // Start a new AnswerFragment with the correct AnswerFormat and layout
        Bundle bundle = new Bundle();
        bundle.putSerializable("format", answerFormat);
        bundle.putSerializable("layout", layout);
        AnswerFragment answerFragment = new AnswerFragment();
        answerFragment.setArguments(bundle);

        FragmentTransaction fragmentTransaction =
                Objects.requireNonNull(getActivity())
                        .getSupportFragmentManager()
                        .beginTransaction();
        fragmentTransaction.add(R.id.answer_fragment_container, answerFragment).commit();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    // Concrete Visitor
    private class Visitor implements AnswerFormat.Visitor {
        @Override
        public void visitNumberField(AnswerFormat.NumberField field) {
            answerFormat = field;
            layout = R.layout.answer_fragment;
        }
    }
}
