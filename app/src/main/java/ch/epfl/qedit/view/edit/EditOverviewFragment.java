package ch.epfl.qedit.view.edit;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import ch.epfl.qedit.R;
import ch.epfl.qedit.model.MatrixFormat;
import ch.epfl.qedit.model.Question;


/**
 * This fragment is used to view and edit the list of questions of a quiz.
 */
public class EditOverviewFragment extends Fragment {
    private RecyclerView recyclerView;
    private List<Question> questions;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_overview, container, false);

        // For now, we just create a dummy list of questions. We will later link it to the rest.
        questions = new LinkedList<>(Arrays.asList(
                new Question("Q1", "why ?", new MatrixFormat(1, 1)),
                new Question("Q2", "how ?", new MatrixFormat(1, 1)),
                new Question("Q3", "what ?", new MatrixFormat(1, 1)),
                new Question("Q4", "when ?", new MatrixFormat(1, 1))
        ));

        for (int i = 5; i <= 20; i++)
            questions.add(new Question("Q" + i, "is it " + i + " ?", new MatrixFormat(1, 1)));

        // Retrieve and configure the recycler view
        recyclerView = view.findViewById(R.id.question_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(new QuestionListAdapter(questions));
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(),
                DividerItemDecoration.VERTICAL));

        return view;
    }

    private static class QuestionListAdapter extends RecyclerView.Adapter<QuestionListAdapter.QuestionHolder> {

        private class QuestionHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private final TextView title;
            private final LinearLayout questionButtons;

            public QuestionHolder(@NonNull View itemView) {
                super(itemView);
                title = itemView.findViewById(android.R.id.text1);
                questionButtons = itemView.findViewById(R.id.question_buttons);
                itemView.setOnClickListener(this);
            }

            public void setTitle(String str) {
                title.setText(str);
            }

            public void setSelected(boolean selected) {
                questionButtons.setVisibility(selected ? View.VISIBLE : View.INVISIBLE);
            }

            @Override
            public void onClick(View v) {
                QuestionListAdapter.this.notifyItemChanged(selectedQuestion);
                selectedQuestion = getLayoutPosition();
                QuestionListAdapter.this.notifyItemChanged(selectedQuestion);
            }
        }

        private final List<Question> questions;
        private int selectedQuestion = RecyclerView.NO_POSITION;

        public QuestionListAdapter(List<Question> questions) {
            this.questions = questions;
        }

        @NonNull
        @Override
        public QuestionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new QuestionHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.question_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull QuestionHolder holder, int position) {
            holder.setTitle(questions.get(position).getTitle());
            holder.setSelected(position == selectedQuestion);
        }

        @Override
        public int getItemCount() {
            return questions.size();
        }
    }
}
