package ch.epfl.qedit.view.edit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import ch.epfl.qedit.R;
import ch.epfl.qedit.model.MatrixFormat;
import ch.epfl.qedit.model.Question;
import ch.epfl.qedit.view.util.ListEditView;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/** This fragment is used to view and edit the list of questions of a quiz. */
public class EditOverviewFragment extends Fragment {
    private RecyclerView recyclerView;
    private List<Question> questions;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_overview, container, false);

        // For now, we just create a dummy list of questions. We will later link it to the rest.
        questions =
                new LinkedList<>(
                        Arrays.asList(
                                new Question("Q1", "why ?", new MatrixFormat(1, 1)),
                                new Question("Q2", "how ?", new MatrixFormat(1, 1)),
                                new Question("Q3", "what ?", new MatrixFormat(1, 1)),
                                new Question("Q4", "when ?", new MatrixFormat(1, 1))));

        for (int i = 5; i <= 20; i++)
            questions.add(new Question("Q" + i, "is it " + i + " ?", new MatrixFormat(1, 1)));

        // Retrieve and configure the recycler view
        ListEditView listEditView = view.findViewById(R.id.question_list);
        listEditView.setAdapter(
                new ListEditView.ListEditAdapter<Question>(
                        questions,
                        new ListEditView.GetItemText<Question>() {
                            @Override
                            public String getText(Question question) {
                                return question.getTitle();
                            }
                        }));

        return view;
    }
}
