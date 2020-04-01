package ch.epfl.qedit.view.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import ch.epfl.qedit.R;

public class ConfirmDialogFragment extends DialogFragment {
    public interface ConfirmationListener {
        public void onConfirm(ConfirmDialogFragment dialog);
    }

    private ConfirmationListener listener;
    private String message;

    public ConfirmDialogFragment(String message, ConfirmationListener listener) {
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
                        listener.onConfirm(ConfirmDialogFragment.this);
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ConfirmDialogFragment.this.dismiss();
                    }
                })
                .create();
    }
}
