package ch.epfl.qedit.view.quiz;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import ch.epfl.qedit.R;
import ch.epfl.qedit.model.Question;
import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.viewmodel.QuizViewModel;
import java.util.List;

/** A simple {@link Fragment} subclass. */
public class QuizOverviewFragment extends Fragment {
    /** The view model from which data is observed */
    private QuizViewModel model;

    private ListView listView;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_quiz_overview, container, false);
        listView = view.findViewById(R.id.question_list);

        // Listen to the quiz live data
        final QuizViewModel model =
                new ViewModelProvider(requireActivity()).get(QuizViewModel.class);
        model.getQuiz()
                .observe(
                        getViewLifecycleOwner(),
                        new Observer<Quiz>() {
                            @Override
                            public void onChanged(Quiz quiz) {
                                if (quiz == null) return;
                                else onDataArrived(quiz);
                            }
                        });

        listView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(
                            AdapterView<?> parent, View view, int position, long id) {
                        // We change the question that is focused when the corresponding list item
                        // is selected
                        model.getFocusedQuestion().postValue(position);
                    }
                });

        return view;
    }

    /** Handles the arrival of the quiz from the database */
    private void onDataArrived(Quiz quiz) {
        List<Question> questions = quiz.getQuestions();
        String[] overviewItems = new String[questions.size()];

        // For each question of the quiz, we create an item in the list view
        for (int i = 0; i < questions.size(); ++i) overviewItems[i] = "Question " + (i + 1);

        ArrayAdapter<String> listViewAdapter =
                new ArrayAdapter<>(
                        requireActivity(), android.R.layout.simple_list_item_1, overviewItems);

        listView.setAdapter(listViewAdapter);
    }
}
