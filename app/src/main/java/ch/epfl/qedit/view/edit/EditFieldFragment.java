package ch.epfl.qedit.view.edit;

import static ch.epfl.qedit.model.answer.MatrixFormat.Field.Type.*;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.fragment.app.DialogFragment;
import ch.epfl.qedit.R;
import ch.epfl.qedit.model.answer.MatrixFormat;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public class EditFieldFragment extends DialogFragment {

    private int maxNbOfChar;
    private boolean isSigned;
    private boolean isDecimal;
    private boolean isText;
    private boolean isPreFilled;
    private String preFilledText;

    /** Layout component * */
    private CheckBox checkBoxDecimal;

    private CheckBox checkBoxSign;
    private TextView preview;
    private Spinner typeSpinner;

    /** Index in the spinner of the different types * */
    private final int NUMBER_TYPE_IDX = 0;

    private final int TEXT_TYPE_IDX = 1;
    private final int PRE_FILLED_TYPE_IDX = 2;

    public EditFieldFragment(boolean isText) {
        this.maxNbOfChar = MatrixFormat.Field.NO_LIMIT;
        this.isText = isText;
        isSigned = false;
        isDecimal = false;
        isPreFilled = false;
        preFilledText = null;
    }

    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = Objects.requireNonNull(getActivity()).getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_edit_field, null);

        initLayoutComponent(view);

        builder.setView(view)
                .setTitle(R.string.edit_field_title)
                .setPositiveButton(R.string.done, null)
                .setNegativeButton(R.string.cancel, null);

        // Create the AlertDialog object and return it
        return builder.create();
    }

    private void initLayoutComponent(View view) {
        checkBoxDecimal = view.findViewById(R.id.decimalCheckBox);
        checkBoxSign = view.findViewById(R.id.signCheckBox);
        preview = view.findViewById(R.id.field_preview);
        typeSpinner = view.findViewById(R.id.field_types_selection);

        int selectSpinner =
                isNumber() ? NUMBER_TYPE_IDX : isPreFilled ? PRE_FILLED_TYPE_IDX : TEXT_TYPE_IDX;
        typeSpinner.setSelection(selectSpinner);

        updateLayout();
    }

    private MatrixFormat.Field getResultingField() {
        if (isPreFilled) return MatrixFormat.Field.preFilledField(preFilledText);
        if (isText) return MatrixFormat.Field.textField(getHint(), maxNbOfChar);
        else return MatrixFormat.Field.numericField(isDecimal, isDecimal, getHint(), maxNbOfChar);
    }

    private void updateLayout() {
        preview.setHint(getHint());

        int checkBoxVisibility = isNumber() ? View.GONE : View.VISIBLE;
        checkBoxDecimal.setVisibility(checkBoxVisibility);
        checkBoxSign.setVisibility(checkBoxVisibility);
    }

    /** @return true if the field want a number answer, False otherwise */
    private boolean isNumber() {
        return !isText && !isPreFilled;
    }

    /** @return the minimum character limit acceptable regarding the type of this field */
    private int getMinCharLimit() {
        int min = 1;
        if (isNumber()) {
            // signed: '-1'
            // decimal: '1.0'
            // decimal and signed: '-1.0'
            min = isSigned ? min + 1 : min;
            min = isDecimal ? min + 2 : min;
        }
        // text or pre filled: 'A'
        return min;
    }

    /**
     * @return the String hint of the text view of this fragment regarding the type of this field
     */
    private String getHint() {
        if (isNumber()) {
            // signed: ±0
            // decimal: 0.0
            // decimal and signed: ±0.0
            String hint = isSigned ? "±0" : "0";
            return isDecimal ? hint + ".0" : hint;
        } else if (isPreFilled) {
            return getString(R.string.hint_pre_filled_field);
        } else { // isText is true
            return "???";
        }
    }
}
