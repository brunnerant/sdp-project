package ch.epfl.qedit.view.edit;

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
import ch.epfl.qedit.model.StringPool;
import ch.epfl.qedit.viewmodel.EditionViewModel;

public class EditPreviewFragment extends Fragment {
    public static final String EDIT_ANSWER_FORMAT = "ch.epfl.qedit.view.edit.EDIT_ANSWER_FORMAT";
    public static final String EDIT_ANSWER_MODEL = "ch.epfl.qedit.view.edit.EDIT_ANSWER_MODEL";
    public static final String EDIT_FRAGMENT_TAG = "ch.epfl.qedit.view.edit.EDIT_FRAGMENT_TAG";

    private TextView questionTitle;
    private TextView questionDisplay;
    private EditionViewModel model;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_quiz_question, container, false);
        questionTitle = view.findViewById(R.id.question_title);
        questionDisplay = view.findViewById(R.id.question_display);

        model = new ViewModelProvider(requireActivity()).get(EditionViewModel.class);

        model.getFocusedQuestion()
                .observe(
                        getViewLifecycleOwner(),
                        new Observer<Integer>() {
                            @Override
                            public void onChanged(Integer index) {
                                onQuestionChanged(index);
                            }
                        });

        return view;
    }

    /** Handles the transition from one question to another */
    private void onQuestionChanged(Integer index) {
        Quiz.Builder quizBuilder = model.getQuizBuilder();

        if (index == null || index < 0 || index >= quizBuilder.size()) {
            questionTitle.setText(null);
            questionDisplay.setText(null);
            return;
        }

        Question question = quizBuilder.getQuestions().get(index);
        StringPool stringPool = model.getStringPool();

        // We have to change the question title and text
        String key =
                question.getTitle(); // TODO Support old questions that store the strings directly
        // as well
        String text = stringPool.get(key);
        questionTitle.setText((text == null) ? key : text);

        key = question.getText();
        text = stringPool.get(key);
        questionDisplay.setText((text == null) ? key : text);
    }
}
