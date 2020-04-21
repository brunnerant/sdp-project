package ch.epfl.qedit.util;

import android.content.Context;
import android.widget.Toast;

public class Error {

    private final int stringId;
    private Toast toast;

    /**
     * @param stringId the id of the error string that correspond to this error, a stringId 0 is
     *     invalid in android API, then we use value 0 to describe a NO_ERROR
     */
    public Error(int stringId) {
        this.stringId = stringId;
    }

    /**
     * @param context context in where to print the toast if 'this' is an error
     * @return true if 'this' is a NO_ERROR, return false and print a toast in the context if 'this'
     *     is indeed an error
     */
    public boolean noError(Context context) {
        if (stringId == 0) {
            return true;
        }

        toast =
                Toast.makeText(
                        context, context.getResources().getString(stringId), Toast.LENGTH_SHORT);
        toast.show();

        return false;
    }

    /**
     * If the toast has been assigned, cancel it before showing a new one or stopping the activity
     */
    public void cancelToast() {
        if (toast != null) toast.cancel();
    }
}
