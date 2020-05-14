package ch.epfl.qedit.view.quiz;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import ch.epfl.qedit.R;
import ch.epfl.qedit.model.Question;
import ch.epfl.qedit.model.StringPool;
import ch.epfl.qedit.viewmodel.QuizViewModel;
import java.util.List;

/** A simple {@link Fragment} subclass. */
public class QuizOverviewFragment extends Fragment {

    private ListView listView;
    private QuizViewModel quizViewModel;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_quiz_overview, container, false);
        listView = view.findViewById(R.id.question_list);

        // Listen to the quiz live data
        quizViewModel = new ViewModelProvider(requireActivity()).get(QuizViewModel.class);
        setupListView();

        listView.setOnItemClickListener(
                (parent, view1, position, id) -> {
                    // We change the question that is focused when the corresponding list item
                    // is selected
                    quizViewModel.getFocusedQuestion().postValue(position);
                });

        return view;
    }

    /** Handles the initialization of the ListView showing the list of questions */
    private void setupListView() {
        List<Question> questions = quizViewModel.getQuiz().getQuestions();
        StringPool stringPool = quizViewModel.getStringPool();
        String[] overviewItems = new String[questions.size()];

        // For each question of the quiz, we create an item in the list view
        for (int i = 0; i < questions.size(); ++i) {
            String title = stringPool.get(questions.get(i).getTitle());
            overviewItems[i] = (i + 1) + ") " + title;
        }

        ArrayAdapter<String> listViewAdapter =
                new ArrayAdapter<>(
                        requireActivity(), android.R.layout.simple_list_item_1, overviewItems);

        listView.setAdapter(listViewAdapter);
    }
}
