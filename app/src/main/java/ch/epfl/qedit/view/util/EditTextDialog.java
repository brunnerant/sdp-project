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

import java.util.Objects;

import ch.epfl.qedit.R;

public class EditTextDialog extends DialogFragment {
    public interface SubmissionListener {
        void onSubmit(String text);
    }

    public interface TextFilter {
        String isAllowed(String text);
    }

    public static final TextFilter NO_FILTER = new TextFilter() {
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

    public EditTextDialog(String message, SubmissionListener listener) {
        this.listener = listener;
        this.message = message;
    }

    public void setTextFilter(TextFilter textFilter) {
        this.textFilter = Objects.requireNonNull(textFilter);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        editText = new EditText(context);
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String error = textFilter.isAllowed(s.toString());
                Button positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);

                if (error != null) {
                    editText.setError(error);
                    positive.setClickable(false);
                } else {
                    positive.setClickable(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        dialog = new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onSubmit(editText.getText().toString());
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditTextDialog.this.dismiss();
                    }
                })
                .setView(editText)
                .create();

        return dialog;
    }
}
