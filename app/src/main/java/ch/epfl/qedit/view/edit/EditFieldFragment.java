package ch.epfl.qedit.view.edit;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import ch.epfl.qedit.model.answer.MatrixFormat;
import ch.epfl.qedit.model.answer.MatrixFormat.Field.Type;

import static ch.epfl.qedit.model.answer.MatrixFormat.Field.Type.*;

public class EditFieldFragment extends DialogFragment {

    private int maxNbOfChar;
    private boolean isSigned;
    private boolean isDecimal;
    private boolean isText;
    private boolean isPreFilled;


    public EditFieldFragment(){
        this.maxNbOfChar = MatrixFormat.Field.NO_LIMIT;
        isSigned = false;
        isDecimal = false;
        isText = false;
        isPreFilled = false;
    }

    private Type getType(){
        Type type;
        if(isPreFilled) {
            type = PreFilled;
        } else if(isText){
            type = Text;
        } else if(isSigned){
            type = (isDecimal)? SignedFloat : SignedInt;
        } else {
            type = (isDecimal)? UnsignedFloat : UnsignedInt;
        }
        return type;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());


        // Create the AlertDialog object and return it
        return builder.create();
    }

}
