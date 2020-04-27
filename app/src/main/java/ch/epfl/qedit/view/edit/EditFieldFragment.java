package ch.epfl.qedit.view.edit;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import ch.epfl.qedit.R;
import ch.epfl.qedit.model.answer.MatrixFormat;
import ch.epfl.qedit.model.answer.MatrixFormat.Field.Type;

import static ch.epfl.qedit.model.answer.MatrixFormat.Field.Type.*;

public class EditFieldFragment extends DialogFragment {

    private int maxNbOfChar;
    private boolean isSigned;
    private boolean isDecimal;
    private boolean isText;
    private boolean isPreFilled;
    private String preFilledText;

    public EditFieldFragment(){
        this.maxNbOfChar = MatrixFormat.Field.NO_LIMIT;
        isSigned = false;
        isDecimal = false;
        isText = false;
        isPreFilled = false;
        preFilledText = null;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Create the AlertDialog object and return it
        return builder.create();
    }

    private MatrixFormat.Field getResultingField(){
        if(isPreFilled)
            return MatrixFormat.Field.preFilledField(preFilledText);
        if(isText)
            return MatrixFormat.Field.textField(getHint(), maxNbOfChar);
        else
            return MatrixFormat.Field.numericField(isDecimal, isDecimal, getHint(), maxNbOfChar);
    }

    /**
     * @return true if the field want a number answer, False otherwise
     */
    private boolean isNumber(){
        return !isText && !isPreFilled;
    }

    /**
     * @return the minimum character limit acceptable regarding the type of this field
     */
    private int getMinCharLimit(){
        int min = 1;
        if(isNumber()){
            // signed: '-1'
            // decimal: '1.0'
            // decimal and signed: '-1.0'
            min = isSigned? min + 1 : min;
            min = isDecimal? min + 2 : min;
        }
        // text or pre filled: 'A'
        return min;
    }

    /**
     * @return the String hint of the text view of this fragment regarding the type of this field
     */
    private String getHint(){
        if(isNumber()){
            // signed: ±0
            // decimal: 0.0
            // decimal and signed: ±0.0
            String hint = isSigned? "±0" : "0";
            return isDecimal? hint + ".0" : hint;
        } else if (isPreFilled) {
            return getString(R.string.hint_pre_filled_field);
        } else { // isText is true
            return "???";
        }
    }

}
