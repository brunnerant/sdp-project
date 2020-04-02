package ch.epfl.qedit.view.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import ch.epfl.qedit.R;
import java.io.Serializable;
import java.util.Objects;

/**
 * This class is used to create a edit text dialog, that is, a dialog that allows the user to enter
 * some text. To create the dialog, use ConfirmDialog.create(message, listener), and to show it use
 * dialog.show(fragmentManager, tag).
 */
public class EditTextDialog extends DialogFragment {
    /** This interface is used to be notified when the user entered the text. */
    public interface SubmissionListener extends Serializable {
        void onSubmit(String text);
    }

    /**
     * This interface is used to filter the allowed text inputs. To allow a string in the dialog, it
     * should return null. To indicate an error, it simply returns the string containing the error
     * message, which will be displayed next to the edit text.
     */
    public interface TextFilter {
        String isAllowed(String text);
    }

    public static final TextFilter NO_FILTER =
            new TextFilter() {
                @Override
                public String isAllowed(String text) {
                    return null;
                }
            };

    private SubmissionListener listener;
    private TextFilter textFilter = NO_FILTER;
    private String message;
    private EditText editText;
    private AlertDialog dialog;

    private EditTextDialog() {}

    /**
     * The edit text dialogs should be created through this method, because Fragments cannot have
     * constructors that take arguments.
     *
     * @param message the message to display above the edit text view
     * @param listener the listener that gets notified once the text was entered
     * @return a new dialog
     */
    public static EditTextDialog create(String message, SubmissionListener listener) {
        EditTextDialog dialog = new EditTextDialog();

        Bundle args = new Bundle();
        args.putString("message", message);
        args.putSerializable("listener", listener);

        dialog.setArguments(args);
        return dialog;
    }

    /**
     * This method allows to validate the text that the user enters in the dialog. The text filter
     * can return an error to display, or null to indicate that the text is valid. The constant
     * NO_FILTER can be used to validate everything.
     *
     * @param textFilter the text filter, non null.
     */
    public void setTextFilter(TextFilter textFilter) {
        this.textFilter = Objects.requireNonNull(textFilter);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        editText = new EditText(context);
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        editText.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(
                            CharSequence s, int start, int count, int after) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        String error = textFilter.isAllowed(s.toString());
                        Button positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);

                        if (error != null) {
                            editText.setError(error);
                            positive.setEnabled(false);
                        } else {
                            positive.setEnabled(true);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {}
                });

        // This is for testing, so that the text can be retrieved by id
        editText.setId(R.id.quiz_name_text);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        message = args.getString("message");
        listener = (SubmissionListener) args.getSerializable("listener");

        dialog =
                new AlertDialog.Builder(getActivity())
                        .setMessage(message)
                        .setPositiveButton(
                                R.string.done,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        listener.onSubmit(editText.getText().toString());
                                    }
                                })
                        .setNegativeButton(
                                R.string.cancel,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        EditTextDialog.this.dismiss();
                                    }
                                })
                        .setView(editText)
                        .create();

        // This is used to trigger the text watcher. Otherwise, it is not triggered
        // until the user enters something
        dialog.setOnShowListener(
                new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        editText.setText("");
                    }
                });

        return dialog;
    }
}
