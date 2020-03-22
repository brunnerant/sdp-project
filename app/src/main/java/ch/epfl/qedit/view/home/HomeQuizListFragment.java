package ch.epfl.qedit.view.home;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
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
import ch.epfl.qedit.model.Question;
import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.model.User;
import ch.epfl.qedit.view.quiz.QuizActivity;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class HomeQuizListFragment extends Fragment {
    public static final String QUIZID = "ch.epfl.qedit.view.QUIZID";

    private RecyclerView recyclerView;
    private CustomAdapter customAdapter;
    private User user;
    private String errorBlank = "Can't be blank";

    // The magic number comes from button color in android
    private final int colorButton = -2614432;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_home_quiz_list, container, false);
        recyclerView = view.findViewById(R.id.home_quiz_list);

        // Get user from the bundle created by the parent activity
        final User user = (User) Objects.requireNonNull(getArguments()).getSerializable("user");
        this.user = user;

        this.customAdapter = new CustomAdapter(requireActivity(), user);
        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));

        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

//        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
//            @Override
//            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
//                return false;
//            }
//
//            @Override
//            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
//                // Remove item from backing list here
//                adapter.notifyDataSetChanged();
                    // TODO Show trash can and edit stuff on other side
//            }
//        });

//        itemTouchHelper.attachToRecyclerView(recyclerView);

        // Have to set it to true to show the menu, if the user is an editor
        if (user.getRole() == User.Role.Editor) {
            setHasOptionsMenu(true);
        }

        return view;
    }

    private void addQuizzes(String title) {
        int index = user.getQuizzes().size();
        user.addQuiz(title, title);
        customAdapter.notifyItemInserted(index);
    }

    private void addPopUp() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add quiz's name");

        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                    addQuizzes(input.getText().toString());
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        final AlertDialog alertDialog = builder.create();
        alertDialog.create();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setClickable(false);
        input.setError(errorBlank);
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE);
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String title = input.getText().toString().trim();

                boolean canAdd = user.canAdd(title);

                if (input.length() <= 0 || !canAdd) {
                    String error = input.length() <= 0 ? errorBlank : "Can't have duplicate names";
                    setAlertError(alertDialog, input, false, Color.WHITE, error);
                } else {
                    setAlertError(alertDialog, input, true, colorButton, null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        alertDialog.show();
    }

    private void setAlertError(AlertDialog alertDialog, EditText editText,
                               boolean isClickable, int color, String error) {
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setClickable(isClickable);
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(color);
        editText.setError(error);
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
                addPopUp();
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

            // TODO Find a better than this...
            final Map.Entry<String, String> entryScrew = new ArrayList<>(user.getQuizzes().entrySet()).get(position);
            customViewHolder.name.setText(entryScrew.getValue().trim());

            customViewHolder.parentView.setOnClickListener(new View.OnClickListener() {
                @Override
                    public void onClick(View v){
                            Map.Entry<String, String> item = entryScrew;
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
            return user.getQuizzes().size();
        }

    private class CustomViewHolder extends RecyclerView.ViewHolder {
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
