package ch.epfl.qedit.view.home;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.widget.EditText;
import androidx.recyclerview.widget.RecyclerView;
import ch.epfl.qedit.model.User;

public class HomePopUp {
    private String errorBlank = "Can't be blank";

    // The magic number comes from button color in android
    private final int colorButton = -2614432;
    private Context context;
    private User user;
    private RecyclerView.Adapter customAdapter;

    public HomePopUp(Context context, User user, RecyclerView.Adapter customAdapter) {
        this.context = context;
        this.user = user;
        this.customAdapter = customAdapter;
    }

    public AlertDialog popUpEdit(final String oldValue, final int position) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Give a new name... Or the same one");

        final EditText input = new EditText(context);
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

        return errorDialog(builder, input);
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

    public AlertDialog errorDialog(AlertDialog.Builder builder, final EditText editText) {
        final AlertDialog alertDialog = builder.create();
        alertDialog.create();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setClickable(false);
        editText.setError(errorBlank);
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE);
        editText.addTextChangedListener(new CustomTextWatcher(editText, alertDialog));

        return alertDialog;
    }

    public AlertDialog popUpWarningDelete(final String title, final int position) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
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
        return alertDialog;
    }

    public void addPopUp() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Add quiz's name");

        final EditText input = new EditText(context);
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

        errorDialog(builder, input).show();
    }

    private void addQuizzes(String title) {
        int index = user.getQuizzes().size();
        user.addQuiz(title, title);
        customAdapter.notifyItemInserted(index);
    }

    private class CustomTextWatcher implements TextWatcher {
        private EditText editText;
        private AlertDialog alertDialog;

        public CustomTextWatcher(EditText editText, AlertDialog alertDialog) {
            this.editText = editText;
            this.alertDialog = alertDialog;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String title = editText.getText().toString().trim();

            boolean canAdd = user.canAdd(title);

            if (editText.length() <= 0 || !canAdd) {
                String error = editText.length() <= 0 ? errorBlank : "Can't have duplicate names";
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setClickable(false);
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE);
                editText.setError(error);
            } else {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setClickable(true);
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(colorButton);
                editText.setError(null);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {}
    }
}
