package ch.epfl.qedit.view.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import ch.epfl.qedit.R;

/**
 * This class is used to create a information dialog, that is, a dialog with only one accept button
 * that allows to close the dialog once the user had time to read the message To create the dialog,
 * use InfoDialog.create(message), and to show it use dialog.show(fragmentManager, tag).
 */
public class InfoDialog extends DialogFragment {
    private InfoDialog() {}

    /**
     * The information dialogs should be created through this method, because Fragments cannot have
     * constructors that take arguments.
     *
     * @param message the information message to display
     * @return a new dialog
     */
    public static InfoDialog create(String message) {
        InfoDialog dialog = new InfoDialog();

        Bundle args = new Bundle();
        args.putString("message", message);

        dialog.setArguments(args);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        String message = args.getString("message");

        return new AlertDialog.Builder(requireActivity())
                .setMessage(message)
                .setNegativeButton(R.string.cancel, (dialog, which) -> InfoDialog.this.dismiss())
                .create();
    }
}
