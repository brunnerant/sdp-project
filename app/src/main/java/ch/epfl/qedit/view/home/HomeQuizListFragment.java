package ch.epfl.qedit.view.home;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import org.w3c.dom.Text;

import ch.epfl.qedit.R;
import ch.epfl.qedit.model.User;
import ch.epfl.qedit.view.quiz.QuizActivity;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class HomeQuizListFragment extends Fragment {
    public static final String QUIZID = "ch.epfl.qedit.view.QUIZID";

    private RecyclerView recyclerView;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_home_quiz_list, container, false);
        recyclerView = view.findViewById(R.id.home_quiz_list);

        // Get user from the bundle created by the parent activity
        final User user = (User) Objects.requireNonNull(getArguments()).getSerializable("user");

        final ArrayList<Map.Entry<String, String>> entries =
                new ArrayList<>(user.getQuizzes().entrySet());

        final CustomAdapter adapter = new CustomAdapter(requireActivity(), entries);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));

        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        //listView.setOnItemClickListener(
//        listView.setOnClickListener(
//                new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(
//                            AdapterView<?> parent, View view, int position, long id) {
//                        Map.Entry<String, String> item =
//                                (Map.Entry<String, String>) adapter.getItem(position);
//
//                        startQuizActivity(item.getKey());
//                    }
//                });


        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                // Remove item from backing list here
                adapter.notifyDataSetChanged();
            }
        });

        itemTouchHelper.attachToRecyclerView(recyclerView);
        // Have to set it to true to show the menu, if the user is an editor
        if (user.getRole() == User.Role.Editor) {
            setHasOptionsMenu(true);
        }

        return view;
    }

    private void addQuizz() {

    }

    private void addPopUp() {}

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_editor_mode, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.add:
                addQuizz();
                break;
        }

        return true;
    }

    private void startQuizActivity(String quizID) {
        Intent intent = new Intent(getActivity(), QuizActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(QUIZID, quizID);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private class CustomAdapter extends RecyclerView.Adapter {
        private ArrayList<Map.Entry<String, String>> entries;
        private LayoutInflater inflater;


        public CustomAdapter(Context context, ArrayList<Map.Entry<String, String>> entries) {
            this.entries = entries;
            inflater = LayoutInflater.from(context);
        }

//        @Override
//        public int getCount() {
//            return entries.size();
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return entries.get(position);
//        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new CustomViewHolder(inflater.inflate(R.layout.recycler_row, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            CustomViewHolder customViewHolder = ((CustomViewHolder)holder);
            customViewHolder.name.setText(entries.get(position).getValue());
            final int position2 = position;
            customViewHolder.parentView.setOnClickListener(new View.OnClickListener() {
                @Override
                    public void onClick(View v){
                            Map.Entry<String, String> item = entries.get(position2);
                        startQuizActivity(item.getKey());
                    }
                });
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return entries.size();
        }

//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            View view = convertView;
//            if (view == null) {
//                view = inflater.inflate(android.R.layout.simple_list_item_1, null);
//            }
//            TextView text = view.findViewById(android.R.id.text1);
//            text.setText(entries.get(position).getValue());
//            return view;
//        }
    public class CustomViewHolder extends RecyclerView.ViewHolder {
        private View parentView;
        private TextView name;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
                parentView = itemView;
                name = itemView.findViewById(R.id.name);
            }
        }
    }
}
