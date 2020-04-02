package ch.epfl.qedit.view.home;

import static ch.epfl.qedit.view.LoginActivity.USER;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
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
    public static final String QUIZ_ID = "ch.epfl.qedit.view.QUIZ_ID";

    private CustomAdapter customAdapter;
    private User user;

    private HomePopUp homePopUp;
    private DatabaseService db;
    private Handler handler;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_home_quiz_list, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.home_quiz_list);

        // Get user from the bundle created by the parent activity
        final User user = (User) Objects.requireNonNull(getArguments()).getSerializable(USER);
        this.user = user;
        // ListView listView = view.findViewById(R.id.home_quiz_list);

        progressBar = view.findViewById(R.id.quiz_loading);

        // Get user from the bundle created by the parent activity

        // Instantiate Handler and the DatabaseService
        db = DatabaseFactory.getInstance();
        handler = new Handler();

        this.customAdapter = new CustomAdapter(requireActivity());
        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));

        recyclerView.addItemDecoration(
                new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        homePopUp = new HomePopUp(getContext(), this.user, customAdapter);

        // Have to set it to true to show the menu, if the user is an editor or administrator
        if (user.getRole() == User.Role.Editor | user.getRole() == User.Role.Administrator) {
            getNewItemTouchHelper().attachToRecyclerView(recyclerView);
            setHasOptionsMenu(true);
        }

        return view;
    }

    private ItemTouchHelper getNewItemTouchHelper() {
        CustomCallBack customCallBack =
                new CustomCallBack(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        return new ItemTouchHelper(customCallBack);
    }

    private void doOnSwipe(RecyclerView.ViewHolder viewHolder, int swipeDir) {
        int position = viewHolder.getAdapterPosition();
        final Map.Entry<String, String> entryScrew =
                new ArrayList<>(user.getQuizzes().entrySet()).get(position);

        if (swipeDir == ItemTouchHelper.LEFT) {
            homePopUp.popUpWarningDelete(entryScrew.getValue(), position).show();
        } else if (swipeDir == ItemTouchHelper.RIGHT) {
            homePopUp.popUpEdit(entryScrew.getValue(), position).show();
        }

        customAdapter.notifyDataSetChanged();
    }

    private RecyclerView.ViewHolder setVisibilityOnChildDraw(
            float dX, CustomAdapter.CustomViewHolder customViewHolder) {
        if (dX > 0) {
            customViewHolder.delete.setVisibility(View.INVISIBLE);
            customViewHolder.edit.setVisibility(View.VISIBLE);
        } else if (dX < 0) {
            customViewHolder.edit.setVisibility(View.INVISIBLE);
            customViewHolder.delete.setVisibility(View.VISIBLE);
        }

        return customViewHolder;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_editor_mode, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                homePopUp.addPopUp();
                break;
            case android.R.id.home:
                getActivity().onBackPressed();
                break;
        }
        return true;
    }

    private void loadQuiz(final String quizID) {
        progressBar.setVisibility(View.VISIBLE);
        // Query quiz questions from the database
        db.getQuiz(
                quizID,
                new Callback<Response<Quiz>>() {
                    @Override
                    public void onReceive(final Response<Quiz> response) {
                        handler.post(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        // Determine what to do when the quiz is loaded or not
                                        progressBar.setVisibility(View.GONE);
                                        if (response.getError().noError(getContext())) {
                                            onLoadingSuccessful(response.getData());
                                        }
                                    }
                                });
                    }
                });
    }

    //
    // If loading a quiz succeeds, pass the Quiz through a Bundle to the QuizActivity, switch to
    // QuizActivity
    //
    private void onLoadingSuccessful(Quiz quiz) {
        if (isDetached()) {}

        Intent intent = new Intent(requireActivity(), QuizActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(QUIZ_ID, quiz);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private class CustomCallBack extends ItemTouchHelper.SimpleCallback {
        CustomCallBack(int dragDirs, int swipeDirs) {
            super(dragDirs, swipeDirs);
        }

        @Override
        public boolean onMove(
                @NonNull RecyclerView recyclerView,
                @NonNull RecyclerView.ViewHolder viewHolder,
                @NonNull RecyclerView.ViewHolder target) {
            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
            doOnSwipe(viewHolder, swipeDir);
        }

        @Override
        public void onChildDrawOver(
                Canvas c,
                RecyclerView recyclerView,
                RecyclerView.ViewHolder viewHolder,
                float dX,
                float dY,
                int actionState,
                boolean isCurrentlyActive) {
            CustomAdapter.CustomViewHolder customViewHolder =
                    (CustomAdapter.CustomViewHolder) viewHolder;
            getDefaultUIUtil()
                    .onDrawOver(
                            c,
                            recyclerView,
                            customViewHolder.name,
                            dX,
                            dY,
                            actionState,
                            isCurrentlyActive);
        }

        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            if (viewHolder != null) {
                CustomAdapter.CustomViewHolder customViewHolder =
                        (CustomAdapter.CustomViewHolder) viewHolder;
                getDefaultUIUtil().onSelected(customViewHolder.name);
            }
        }

        @Override
        public void onChildDraw(
                Canvas c,
                RecyclerView recyclerView,
                RecyclerView.ViewHolder viewHolder,
                float dX,
                float dY,
                int actionState,
                boolean isCurrentlyActive) {
            CustomAdapter.CustomViewHolder customViewHolder =
                    (CustomAdapter.CustomViewHolder) viewHolder;
            setVisibilityOnChildDraw(dX, customViewHolder);
            getDefaultUIUtil()
                    .onDraw(
                            c,
                            recyclerView,
                            customViewHolder.name,
                            dX,
                            dY,
                            actionState,
                            isCurrentlyActive);
        }
    }

    public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {
        private LayoutInflater inflater;

        public CustomAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        @NonNull
        @Override
        public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new CustomViewHolder(inflater.inflate(R.layout.recycler_row, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
            final Map.Entry<String, String> entryScrew =
                    new ArrayList<>(user.getQuizzes().entrySet()).get(position);
            holder.name.setText(entryScrew.getValue().trim());
            holder.delete.isShown();

            holder.parentView.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            loadQuiz(entryScrew.getKey());
                        }
                    });
        }

        @Override
        public int getItemCount() {
            return user.getQuizzes().size();
        }

        class CustomViewHolder extends RecyclerView.ViewHolder {
            private View parentView;
            private TextView name;
            private TextView delete;
            private TextView edit;

            CustomViewHolder(@NonNull View itemView) {
                super(itemView);
                parentView = itemView;
                name = itemView.findViewById(R.id.name);
                delete = itemView.findViewById(R.id.delete);
                edit = itemView.findViewById(R.id.edit);
            }
        }
    }
}

//    public static final String QUIZ_ID = "ch.epfl.qedit.view.QUIZ_ID";
//
//    private DatabaseService db;
//    private Handler handler;
//    private ProgressBar progressBar;
//
//    @Override
//    public View onCreateView(
//            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        final View view = inflater.inflate(R.layout.fragment_home_quiz_list, container, false);
//        ListView listView = view.findViewById(R.id.home_quiz_list);
//
//        progressBar = view.findViewById(R.id.quiz_loading);
//
//        // Get user from the bundle created by the parent activity
//        final User user = (User) Objects.requireNonNull(getArguments()).getSerializable(USER);
//
//        // Instantiate Handler and the DatabaseService
//        db = DatabaseFactory.getInstance();
//        handler = new Handler();
//
//        ArrayList<Map.Entry<String, String>> entries =
//                new ArrayList<>(user.getQuizzes().entrySet());
//
//        final CustomAdapter adapter = new CustomAdapter(requireActivity(), entries);
//
//        listView.setAdapter(adapter);
//        listView.setOnItemClickListener(
//                new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(
//                            AdapterView<?> parent, View view, int position, long id) {
//                        Map.Entry<String, String> item =
//                                (Map.Entry<String, String>) adapter.getItem(position);
//
//                        loadQuiz(item.getKey());
//                    }
//                });
//
//        return view;
//    }
//
//    private void loadQuiz(final String quizID) {
//        progressBar.setVisibility(View.VISIBLE);
//        /** Query quiz questions from the database */
//        db.getQuiz(
//                quizID,
//                new Callback<Response<Quiz>>() {
//                    @Override
//                    public void onReceive(final Response<Quiz> response) {
//                        handler.post(
//                                new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        /** Determine what to do when the quiz is loaded or not */
//                                        progressBar.setVisibility(View.GONE);
//                                        if (response.getError().noError(getContext())) {
//                                            onLoadingSuccessful(response.getData());
//                                        }
//                                    }
//                                });
//                    }
//                });
//    }
//
//    /**
//     * If loading a quiz succeeds, pass the Quiz through a Bundle to the QuizActivity, switch to
//     * QuizActivity
//     */
//    private void onLoadingSuccessful(Quiz quiz) {
//        if (isDetached()) {}
//
//        Intent intent = new Intent(requireActivity(), QuizActivity.class);
//        Bundle bundle = new Bundle();
//        bundle.putSerializable(QUIZ_ID, quiz);
//        intent.putExtras(bundle);
//        startActivity(intent);
//    }
//
//    private class CustomAdapter extends BaseAdapter {
//        private ArrayList<Map.Entry<String, String>> entries;
//        private LayoutInflater inflater;
//
//        public CustomAdapter(Context context, ArrayList<Map.Entry<String, String>> entries) {
//            this.entries = entries;
//            inflater = LayoutInflater.from(context);
//        }
//
//        @Override
//        public int getCount() {
//            return entries.size();
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return entries.get(position);
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
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
//    }
// }
