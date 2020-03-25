package ch.epfl.qedit.view.home;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
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
import android.widget.EditText;
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

        this.customAdapter = new CustomAdapter(requireActivity());
        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));

        recyclerView.addItemDecoration(
                new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        getNewItemTouchHelper().attachToRecyclerView(recyclerView);

        // Have to set it to true to show the menu, if the user is an editor
        if (user.getRole() == User.Role.Editor) {
            setHasOptionsMenu(true);
        }

        return view;
    }

    private ItemTouchHelper getNewItemTouchHelper() {
        ItemTouchHelper itemTouchHelper =
                new ItemTouchHelper(
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
                                CustomAdapter.CustomViewHolder customViewHolder =
                                        (CustomAdapter.CustomViewHolder) viewHolder;
                                int position = viewHolder.getAdapterPosition();
                                final Map.Entry<String, String> entryScrew =
                                        new ArrayList<>(user.getQuizzes().entrySet()).get(position);

                                if (swipeDir == ItemTouchHelper.LEFT) {
                                    popUpWarningDelete(entryScrew.getValue(), position);
                                } else if (swipeDir == ItemTouchHelper.RIGHT) {
                                    popUpEdit(entryScrew.getValue(), position);
                                }

                                customAdapter.notifyDataSetChanged();
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
                                if (dX > 0) {
                                    customViewHolder.delete.setVisibility(View.INVISIBLE);
                                    customViewHolder.edit.setVisibility(View.VISIBLE);
                                } else if (dX < 0) {
                                    customViewHolder.edit.setVisibility(View.INVISIBLE);
                                    customViewHolder.delete.setVisibility(View.VISIBLE);
                                }
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
                        });

        return itemTouchHelper;
    }

    private void addQuizzes(String title) {
        int index = user.getQuizzes().size();
        user.addQuiz(title, title);
        customAdapter.notifyItemInserted(index);
    }

    private void popUpEdit(final String oldValue, final int position) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Give a new name... Or the same one");

        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        setNegativeButton(builder);

        builder.setPositiveButton(
                "Done",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        user.updateQuizOnValue(oldValue, input.getText().toString());
                        customAdapter.notifyItemChanged(position);
                    }
                });

        erorDialog(builder, input).show();
    }

    private void setNegativeButton(AlertDialog.Builder builder) {
        builder.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
    }

    private void popUpWarningDelete(final String title, final int position) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(
                "Are you sure you want to delete, deleting will delete all questions from this quiz");

        setNegativeButton(builder);

        builder.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        user.removeQuizOnValue(title);
                        customAdapter.notifyItemRemoved(position);
                    }
                });

        final AlertDialog alertDialog = builder.create();
        alertDialog.create();
        alertDialog.show();
    }

    private void addPopUp() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add quiz's name");

        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        setNegativeButton(builder);

        builder.setPositiveButton(
                "Done",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addQuizzes(input.getText().toString());
                    }
                });

        erorDialog(builder, input).show();
    }

    private AlertDialog erorDialog(AlertDialog.Builder builder, final EditText editText) {
        final AlertDialog alertDialog = builder.create();
        alertDialog.create();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setClickable(false);
        editText.setError(errorBlank);
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE);
        editText.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(
                            CharSequence s, int start, int count, int after) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        String title = editText.getText().toString().trim();

                        boolean canAdd = user.canAdd(title);

                        if (editText.length() <= 0 || !canAdd) {
                            String error =
                                    editText.length() <= 0
                                            ? errorBlank
                                            : "Can't have duplicate names";
                            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setClickable(false);
                            alertDialog
                                    .getButton(AlertDialog.BUTTON_POSITIVE)
                                    .setTextColor(Color.WHITE);
                            editText.setError(error);
                        } else {
                            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setClickable(true);
                            alertDialog
                                    .getButton(AlertDialog.BUTTON_POSITIVE)
                                    .setTextColor(colorButton);
                            editText.setError(null);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {}
                });

        return alertDialog;
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

        public CustomAdapter(Context context) {
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
        public long getItemId(int position) {
            return position;
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
