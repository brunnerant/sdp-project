package ch.epfl.qedit.view.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import ch.epfl.qedit.R;
import ch.epfl.qedit.model.User;
import ch.epfl.qedit.view.quiz.QuizActivity;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class HomeQuizListFragment extends Fragment {
    public static final String QUIZID = "ch.epfl.qedit.view.QUIZID";

    private ListView listView;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_home_quiz_list, container, false);
        listView = view.findViewById(R.id.home_quiz_list);

        // Get user from the bundle created by the parent activity
        final User user = (User) Objects.requireNonNull(getArguments()).getSerializable("user");

        ArrayList<Map.Entry<String, String>> entries =
                new ArrayList<>(user.getQuizzes().entrySet());

        final CustomAdapter adapter =
                new CustomAdapter(
                        requireActivity(),
                        entries); // TODO order does change as it comes from a set

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(
                            AdapterView<?> parent, View view, int position, long id) {
                        Map.Entry<String, String> item =
                                (Map.Entry<String, String>) adapter.getItem(position);

                        startQuizActivity(item.getKey());
                    }
                });

        return view;
    }

    private void startQuizActivity(String quizID) {
        Intent intent = new Intent(getActivity(), QuizActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(QUIZID, quizID);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private class CustomAdapter extends BaseAdapter {
        private ArrayList<Map.Entry<String, String>> entries;
        private LayoutInflater inflater;

        public CustomAdapter(Context context, ArrayList<Map.Entry<String, String>> entries) {
            this.entries = entries;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return entries.size();
        }

        @Override
        public Object getItem(int position) {
            return entries.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = inflater.inflate(android.R.layout.simple_list_item_1, null);
            }
            TextView text = (TextView) view.findViewById(android.R.id.text1);
            text.setText(entries.get(position).getValue());
            return view;
        }
    }
}
