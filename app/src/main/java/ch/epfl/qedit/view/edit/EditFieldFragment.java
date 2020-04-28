package ch.epfl.qedit.view.edit;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.fragment.app.DialogFragment;
import ch.epfl.qedit.R;
import ch.epfl.qedit.model.answer.MatrixFormat;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import it.sephiroth.android.library.numberpicker.NumberPicker;

public class EditFieldFragment extends DialogFragment {

    /** id of the boolean argument passed through a bundle at creation of a EditFieldCreation*/
    private static final String IS_TEXT_ARG = "ch.epfl.qedit.view.edit.IS_TEXT_ARG";

    /** State variables, we determine the correct type of Field to create from this variable*/
    private int maxNbOfChar;
    private boolean isSigned;
    private boolean isDecimal;
    private boolean isText;
    private boolean isPreFilled;
    private String preFilledText;

    /** Layout component */
    private CheckBox decimalCheckBox;
    private CheckBox signCheckBox;
    private CheckBox noLimitCheckBox;
    private EditText preview;
    private Spinner typeSpinner;
    private NumberPicker limitPicker;

    /** Index in the spinner of the different types */
    private final int NUMBER_TYPE_IDX = 0;
    private final int TEXT_TYPE_IDX = 1;
    private final int PRE_FILLED_TYPE_IDX = 2;

    public EditFieldFragment() {
        this.maxNbOfChar = MatrixFormat.Field.NO_LIMIT;
        isText = false;
        isSigned = false;
        isDecimal = false;
        isPreFilled = false;
        preFilledText = null;
    }

    /**
     * static factory method that create a new instance of an EditFieldFragment
     *
     * @param isText boolean argument passed through a bundle to the new Fragment
     * @return a EditFieldFragment with the default parameters set in function of isText
     */
    public static EditFieldFragment newInstance(boolean isText) {
        EditFieldFragment dialogue = new EditFieldFragment();

        Bundle args = new Bundle();
        args.putBoolean(IS_TEXT_ARG, isText);
        dialogue.setArguments(args);
        return dialogue;
    }

    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Get isText parameter from the bundle set in the newInstance function
        isText = Objects.requireNonNull(getArguments()).getBoolean(IS_TEXT_ARG);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // inflate custom layout
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

        // find layout component in the layout
        decimalCheckBox = view.findViewById(R.id.decimalCheckBox);
        signCheckBox = view.findViewById(R.id.signCheckBox);
        preview = view.findViewById(R.id.field_preview);
        typeSpinner = view.findViewById(R.id.field_types_selection);
        limitPicker = view.findViewById(R.id.limitPicker);
        noLimitCheckBox = view.findViewById(R.id.noLimitCheckBox);

        // create listeners for each check box
        setDecimalCheckboxListener();
        setSignCheckboxListener();
        setNoLimitCheckboxListener();

        setTypesSpinnerListener();
        setLimitPickerListener();

        limitPicker.setVisibility(View.GONE);
        noLimitCheckBox.setChecked(true);

        // set spinner to initial value
        int selectSpinner = isNumber() ?  NUMBER_TYPE_IDX : isPreFilled ? PRE_FILLED_TYPE_IDX : TEXT_TYPE_IDX;
        typeSpinner.setSelection(selectSpinner);

        updateLayout();
    }

    private void setLimitPickerListener(){
        limitPicker.setNumberPickerChangeListener(new NumberPicker.OnNumberPickerChangeListener(){

            @Override
            public void onStopTrackingTouch(@NotNull NumberPicker numberPicker) { }

            @Override
            public void onStartTrackingTouch(@NotNull NumberPicker numberPicker) { }

            @Override
            public void onProgressChanged(@NotNull NumberPicker numberPicker, int i, boolean b) {
                maxNbOfChar = i;
            }
        });
    }

    private void setTypesSpinnerListener(){
        typeSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        isText = position == TEXT_TYPE_IDX;
                        isPreFilled = position == PRE_FILLED_TYPE_IDX;
                        updateLayout();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) { }

                });
    }

    private void setNoLimitCheckboxListener(){
        noLimitCheckBox.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean checked = ((CheckBox) v).isChecked();
                        if (checked) {
                            maxNbOfChar = MatrixFormat.Field.NO_LIMIT;
                            limitPicker.setVisibility(View.GONE);
                        } else {
                            maxNbOfChar = getMinCharLimit();
                            limitPicker.setProgress(maxNbOfChar);
                            limitPicker.setVisibility(View.VISIBLE);
                        }
                    }
                }
        );
    }

    private void setDecimalCheckboxListener(){
        decimalCheckBox.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isDecimal = ((CheckBox) v).isChecked();
                        updateLayout();
                    }
                }
        );
    }

    private void setSignCheckboxListener(){
        signCheckBox.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isSigned = ((CheckBox) v).isChecked();
                        updateLayout();
                    }
                }
        );
    }


    /**
     * This method is call when we need to return the Field constructed with this dialogue fragment
     *
     * @return MatrixFormat.Field corresponding to the current state of the parameter of this fragment
     */
    private MatrixFormat.Field getResultingField() {
        if (isPreFilled) return MatrixFormat.Field.preFilledField(preFilledText);
        if (isText) return MatrixFormat.Field.textField(getHint(), maxNbOfChar);
        else return MatrixFormat.Field.numericField(isDecimal, isDecimal, getHint(), maxNbOfChar);
    }

    /**
     * Update the layout in function of the current state of the fragment,
     * the update concern:
     * - the hint in the preview
     * - if the preview is usable
     * - the visibility of the decimal and sign checkbox
     * - minimum value of the limitPicker
     */
    private void updateLayout() {

        // update preview
        preview.setHint(getHint());
        // the preview allow the user to enter some text
        // only if it is a pre filled field
        preview.setEnabled(isPreFilled);
        preview.setText(""); // empty the previous to see the hint if is not pre filled

        // update checkbox visibility
        int checkBoxVisibility = isNumber() ? View.VISIBLE : View.GONE;
        decimalCheckBox.setVisibility(checkBoxVisibility);
        signCheckBox.setVisibility(checkBoxVisibility);

        // update maxNbOfChar and limitPicker
        int min = getMinCharLimit();
        maxNbOfChar = Math.max(maxNbOfChar, min);
        limitPicker.setMinValue(min);
        limitPicker.setProgress(maxNbOfChar);
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
