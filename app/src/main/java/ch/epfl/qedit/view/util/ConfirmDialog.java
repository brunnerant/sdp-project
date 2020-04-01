package ch.epfl.qedit.view.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import ch.epfl.qedit.R;

public class ConfirmDialog extends DialogFragment {
    public interface ConfirmationListener {
        void onConfirm(ConfirmDialog dialog);
    }

    private ConfirmationListener listener;
    private String message;

    public ConfirmDialog(String message, ConfirmationListener listener) {
        this.listener = listener;
        this.message = message;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onConfirm(ConfirmDialog.this);
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ConfirmDialog.this.dismiss();
                    }
                })
                .create();
    }
}
