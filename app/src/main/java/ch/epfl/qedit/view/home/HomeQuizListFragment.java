package ch.epfl.qedit.view.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import ch.epfl.qedit.R;
import ch.epfl.qedit.model.User;
import ch.epfl.qedit.view.quiz.QuizActivity;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class HomeQuizListFragment extends Fragment {
    public static final String QUIZID = "ch.epfl.qedit.view.QUIZID";

    private CustomAdapter customAdapter;
    private User user;

    private HomePopUp homePopUp;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_home_quiz_list, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.home_quiz_list);

        // Get user from the bundle created by the parent activity
        final User user = (User) Objects.requireNonNull(getArguments()).getSerializable("user");
        this.user = user;

        this.customAdapter = new CustomAdapter(requireActivity(), user);
        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));

        recyclerView.addItemDecoration(
                new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        homePopUp = new HomePopUp(getContext(), this.user, customAdapter);
        // Have to set it to true to show the menu, if the user is an editor
        if (user.getRole() == User.Role.Editor) {
            getNewItemTouchHelper().attachToRecyclerView(recyclerView);
            setHasOptionsMenu(true);
        }

        return view;
    }

    private ItemTouchHelper getNewItemTouchHelper() {
        ItemTouchHelper.SimpleCallback simpleCallback =
                new ItemTouchHelper.SimpleCallback(
                        0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
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
                    public void onSelectedChanged(
                            RecyclerView.ViewHolder viewHolder, int actionState) {
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
                };

        return new ItemTouchHelper(simpleCallback);
    }

    private void doOnSwipe(RecyclerView.ViewHolder viewHolder, int swipeDir) {
        int position = viewHolder.getAdapterPosition();
        final Map.Entry<String, String> entryScrew =
                new ArrayList<>(user.getQuizzes().entrySet()).get(position);

        if (swipeDir == ItemTouchHelper.LEFT) {
            homePopUp.popUpWarningDelete(entryScrew.getValue(), position);
        } else if (swipeDir == ItemTouchHelper.RIGHT) {
            homePopUp.popUpEdit(entryScrew.getValue(), position);
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

    public class CustomAdapter extends RecyclerView.Adapter {
        private LayoutInflater inflater;
        private User user;

        public CustomAdapter(Context context, User user) {
            this.user = user;
            inflater = LayoutInflater.from(context);
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new CustomViewHolder(inflater.inflate(R.layout.recycler_row, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            CustomViewHolder customViewHolder = (CustomViewHolder) holder;

            // TODO Find a better way than this...
            final Map.Entry<String, String> entryScrew =
                    new ArrayList<>(user.getQuizzes().entrySet()).get(position);
            customViewHolder.name.setText(entryScrew.getValue().trim());
            customViewHolder.delete.isShown();

            customViewHolder.parentView.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Map.Entry<String, String> item = entryScrew;
                            startQuizActivity(item.getKey());
                        }
                    });
        }

        @Override
        public int getItemCount() {
            return user.getQuizzes().size();
        }

        public class CustomViewHolder extends RecyclerView.ViewHolder {
            private View parentView;
            private TextView name;
            private TextView delete;
            private TextView edit;

            public CustomViewHolder(@NonNull View itemView) {
                super(itemView);
                parentView = itemView;
                name = itemView.findViewById(R.id.name);
                delete = itemView.findViewById(R.id.delete);
                edit = itemView.findViewById(R.id.edit);
            }
        }
    }
}
