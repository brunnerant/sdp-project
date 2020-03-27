package ch.epfl.qedit.view.home;

import static ch.epfl.qedit.view.LoginActivity.USER;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import ch.epfl.qedit.R;
import ch.epfl.qedit.backend.database.DatabaseFactory;
import ch.epfl.qedit.backend.database.DatabaseService;
import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.model.User;
import ch.epfl.qedit.util.Callback;
import ch.epfl.qedit.util.Response;
import ch.epfl.qedit.view.quiz.QuizActivity;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class HomeQuizListFragment extends Fragment {
    public static final String QUIZID = "ch.epfl.qedit.view.QUIZID";

    private DatabaseService db;
    private Handler handler;
    private ProgressBar progressBar;

    private ListView listView;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_home_quiz_list, container, false);
        listView = view.findViewById(R.id.home_quiz_list);

        progressBar = view.findViewById(R.id.quiz_loading);

        // Get user from the bundle created by the parent activity
        final User user = (User) Objects.requireNonNull(getArguments()).getSerializable(USER);

        // Instantiate Handler and the DatabaseService
        db = DatabaseFactory.getInstance();
        handler = new Handler();

        ArrayList<Map.Entry<String, String>> entries =
                new ArrayList<>(user.getQuizzes().entrySet());

        final CustomAdapter adapter = new CustomAdapter(requireActivity(), entries);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(
                            AdapterView<?> parent, View view, int position, long id) {
                        Map.Entry<String, String> item =
                                (Map.Entry<String, String>) adapter.getItem(position);

                        loadQuiz(item.getKey());
                    }
                });

        return view;
    }

    private void loadQuiz(final String quizID) {
        progressBar.setVisibility(View.VISIBLE);
        /** Query quiz questions from the database */
        db.getQuiz(
                quizID,
                new Callback<Response<Quiz>>() {
                    @Override
                    public void onReceive(final Response<Quiz> response) {
                        handler.post(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        /** Determine what to do when the quiz is loaded or not */
                                        progressBar.setVisibility(View.GONE);
                                        if (response.successful())
                                            onLoadingSuccessful(response.getData());
                                        else onLoadingFailed(response.getError());
                                    }
                                });
                    }
                });
    }

    /**
     * If loading a quiz succeeds, pass the Quiz through a Bundle to the QuizActivity, switch to
     * QuizActivity
     */
    private void onLoadingSuccessful(Quiz quiz) {
        if (isDetached()) {}

        Intent intent = new Intent(requireActivity(), QuizActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(QUIZID, quiz);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /** If loading a quiz fails */
    private void onLoadingFailed(int error) {
        int stringId = 0;
        switch (error) {
            case DatabaseService.CONNECTION_ERROR:
                stringId = R.string.connection_error_message;
                break;
            case DatabaseService.WRONG_DOCUMENT:
                stringId = R.string.wrong_quiz_id_message;
                break;
            default: // TODO handle WRONG_COLLECTION
                break;
        }
        Toast toast =
                Toast.makeText(
                        requireActivity(), getResources().getString(stringId), Toast.LENGTH_SHORT);
        toast.show();
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
