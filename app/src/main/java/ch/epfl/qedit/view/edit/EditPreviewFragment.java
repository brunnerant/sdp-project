package ch.epfl.qedit.view.edit;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static ch.epfl.qedit.view.quiz.QuestionFragment.ANSWER_FORMAT;
import static ch.epfl.qedit.view.quiz.QuestionFragment.ANSWER_MODEL;
import static ch.epfl.qedit.view.quiz.QuestionFragment.FRAGMENT_TAG;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.lifecycle.ViewModelProvider;
import ch.epfl.qedit.R;
import ch.epfl.qedit.model.Question;
import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.model.answer.AnswerFormat;
import ch.epfl.qedit.model.answer.AnswerModel;
import ch.epfl.qedit.viewmodel.EditionViewModel;

/** This class is used the show a preview of the currently selected question. */
public class EditPreviewFragment extends Fragment {
    private TextView questionTitle;
    private TextView questionDisplay;
    private FragmentContainerView fragmentContainerView;
    private EditionViewModel model;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_quiz_question, container, false);

        // Initialize the two TextViews and the container
        questionTitle = view.findViewById(R.id.question_title);
        questionDisplay = view.findViewById(R.id.question_display);
        fragmentContainerView = view.findViewById(R.id.answer_fragment_container);

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
            fragmentContainerView.setVisibility(GONE);
            return;
        }

        // Get resources from the ViewModel
        Question question =
                quizBuilder.getQuestions().get(index).instantiateLanguage(model.getStringPool());

        // Update EditTexts
        questionTitle.setText(question.getTitle());
        questionDisplay.setText(question.getText());

        // Set everything up for the concrete AnswerFragment and launch it
        prepareAnswerFormatFragment(question);
    }

    /**
     * This method gets the concrete AnswerFormat and AnswerModel, prepares a bundle, then it
     * dispatches the correct Fragment class and finally starts it.
     *
     * @param question The question that is going to be shown
     */
    private void prepareAnswerFormatFragment(Question question) {
        // Make the AnswerFragment visible again if it wasn't already
        if (fragmentContainerView.getVisibility() == GONE) {
            fragmentContainerView.setVisibility(VISIBLE);
        }

        // Get the AnswerFormat and the AnswerModel of the question
        AnswerFormat answerFormat = question.getFormat();
        AnswerModel answerModel = answerFormat.getEmptyAnswerModel();

        // Prepare the bundle for the Fragment
        Bundle bundle = new Bundle();
        bundle.putSerializable(ANSWER_FORMAT, answerFormat);
        bundle.putSerializable(ANSWER_MODEL, answerModel);

        // Get the fragment that matches the concrete type of AnswerFormat
        Fragment fragment = answerFormat.getAnswerFragment();
        fragment.setArguments(bundle);

        // And dynamically instantiate the fragment
        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.answer_fragment_container, fragment, FRAGMENT_TAG)
                .commit();
    }
}
