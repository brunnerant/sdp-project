package ch.epfl.qedit.view.question;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.fragment.app.Fragment;
import ch.epfl.qedit.R;
import ch.epfl.qedit.model.Quiz;
import java.util.Objects;

/** A simple {@link Fragment} subclass. */
public class QuizOverviewFragment extends Fragment {
    private Quiz quiz;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.quiz_overview_fragment, container, false);

        quiz = (Quiz) Objects.requireNonNull(getArguments()).getSerializable("quiz");

        // Create the overview
        int nbOfQuestions = Objects.requireNonNull(quiz).getNbOfQuestions();
        String[] overviewItems = new String[nbOfQuestions];

        for (int i = 0; i < nbOfQuestions; ++i) {
            overviewItems[i] = "Question " + (i + 1);
        }

        ListView listView = view.findViewById(R.id.questionList);
        ArrayAdapter<String> listViewAdapter =
                new ArrayAdapter<>(
                        Objects.requireNonNull(getActivity()),
                        android.R.layout.simple_list_item_1,
                        overviewItems);

        listView.setAdapter(listViewAdapter);

        // Reaction when we click on an item
        clickAction(listView);

        return view;
    }

    private void clickAction(ListView listView) {
        listView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    // parent: our listView
                    // view: item within the list that we clicked on
                    // position: index of the item that was clicked on
                    @Override
                    public void onItemClick(
                            AdapterView<?> parent, View view, int position, long id) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("question", quiz.getQuestions().get(position));
                        QuestionFragment frag = new QuestionFragment();
                        frag.setArguments(bundle);

                        Objects.requireNonNull(getActivity())
                                .getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.question_frame, frag)
                                .commit();
                    }
                });
    }

}
