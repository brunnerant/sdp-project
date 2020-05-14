package ch.epfl.qedit.view.edit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import ch.epfl.qedit.R;
import ch.epfl.qedit.model.Question;
import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.model.StringPool;
import ch.epfl.qedit.viewmodel.EditionViewModel;

/** This class is used the show a preview of the currently selected question. */
public class EditPreviewFragment extends Fragment {
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

        // Initialize the two TextViews
        questionTitle = view.findViewById(R.id.question_title);
        questionDisplay = view.findViewById(R.id.question_display);

        // Get the ViewModel and fix what happens when the focused question changes
        model = new ViewModelProvider(requireActivity()).get(EditionViewModel.class);
        model.getFocusedQuestion().observe(getViewLifecycleOwner(), this::onQuestionChanged);

        return view;
    }

    /** Handles the transition from one question to another */
    private void onQuestionChanged(Integer index) {
        Quiz.Builder quizBuilder = model.getQuizBuilder();

        if (index == null || index < 0 || index >= quizBuilder.size()) {
            // Show nothing
            questionTitle.setText(null);
            questionDisplay.setText(null);
            return;
        }

        // Get resources from the ViewModel
        Question question = quizBuilder.getQuestions().get(index);
        StringPool stringPool = model.getStringPool();

        // Update EditText
        questionTitle.setText(stringPool.get(question.getTitle()));
        questionDisplay.setText(stringPool.get(question.getText()));
    }
}
