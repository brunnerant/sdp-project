package ch.epfl.qedit.view.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import ch.epfl.qedit.R;
import java.io.Serializable;

/**
 * This class is used to create a confirmation dialog, that is, a dialog with a yes and a no button.
 * To create the dialog, use ConfirmDialog.create(message, listener), and to show it use
 * dialog.show(fragmentManager, tag).
 */
public class ConfirmDialog extends DialogFragment {
    /** This interface is used to be notified when the user confirmed the dialog. */
    public interface ConfirmationListener extends Serializable {
        void onConfirm(ConfirmDialog dialog);
    }

    private ConfirmationListener listener;
    private String message;

    private ConfirmDialog() {}

    /**
     * The confirmation dialogs should be created through this method, because Fragments cannot have
     * constructors that take arguments.
     *
     * @param message the confirmation message to display
     * @param listener the listener that gets notified once confirmation occured
     * @return a new dialog
     */
    public static ConfirmDialog create(String message, ConfirmationListener listener) {
        ConfirmDialog dialog = new ConfirmDialog();

        Bundle args = new Bundle();
        args.putString("message", message);
        args.putSerializable("listener", listener);

        dialog.setArguments(args);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        message = args.getString("message");
        listener = (ConfirmationListener) args.getSerializable("listener");

        return new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton(
                        R.string.yes,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                listener.onConfirm(ConfirmDialog.this);
                            }
                        })
                .setNegativeButton(
                        R.string.no,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ConfirmDialog.this.dismiss();
                            }
                        })
                .create();
    }
}
