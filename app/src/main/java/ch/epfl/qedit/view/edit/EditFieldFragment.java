package ch.epfl.qedit.view.edit;

import static ch.epfl.qedit.model.answer.MatrixFormat.Field.NO_LIMIT;

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
import ch.epfl.qedit.model.answer.MatrixFormat.Field;
import java.util.Objects;

public class EditFieldFragment extends DialogFragment {

    /** id of the boolean argument passed through a bundle at creation of a EditFieldCreation */
    private static final String IS_TEXT_ARG = "ch.epfl.qedit.view.edit.IS_TEXT_ARG";

    /** State variables, we determine the correct type of Field to create from this variable */
    private boolean isSigned;

    private boolean isDecimal;
    private boolean isText;
    private boolean isPreFilled;
    private String preFilledText;

    /** Layout component */
    private CheckBox decimalCheckbox;

    private CheckBox signCheckbox;
    private EditText preview;
    private Spinner typesSpinner;

    /** Index in the spinner of the different types */
    public static final int NUMBER_TYPE_IDX = 0;

    public static final int TEXT_TYPE_IDX = 1;
    public static final int PRE_FILLED_TYPE_IDX = 2;

    public EditFieldFragment() {
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
        EditFieldFragment dialog = new EditFieldFragment();

        Bundle args = new Bundle();
        args.putBoolean(IS_TEXT_ARG, isText);
        dialog.setArguments(args);
        return dialog;
    }

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

    /**
     * Init all the layout component with listener and default parameters
     *
     * @param view View from which we retrieve layout component
     */
    private void initLayoutComponent(View view) {

        // find layout component in the layout
        decimalCheckbox = view.findViewById(R.id.decimalCheckBox);
        signCheckbox = view.findViewById(R.id.signCheckBox);
        preview = view.findViewById(R.id.field_preview);
        typesSpinner = view.findViewById(R.id.field_types_selection);

        // create listeners for each check box
        setDecimalCheckboxListener();
        setSignCheckboxListener();

        setTypesSpinnerListener();

        // set spinner to initial value
        int selectSpinner =
                isNumber() ? NUMBER_TYPE_IDX : isPreFilled ? PRE_FILLED_TYPE_IDX : TEXT_TYPE_IDX;
        typesSpinner.setSelection(selectSpinner);

        updateLayout();
    }

    private void setTypesSpinnerListener() {
        typesSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent, View view, int position, long id) {
                        isText = position == TEXT_TYPE_IDX;
                        isPreFilled = position == PRE_FILLED_TYPE_IDX;
                        updateLayout();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                });
    }

    private void setDecimalCheckboxListener() {
        decimalCheckbox.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isDecimal = ((CheckBox) v).isChecked();
                        updateLayout();
                    }
                });
    }

    private void setSignCheckboxListener() {
        signCheckbox.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isSigned = ((CheckBox) v).isChecked();
                        updateLayout();
                    }
                });
    }

    /**
     * This method is call when we need to return the Field constructed with this dialogue fragment
     *
     * @return MatrixFormat.Field corresponding to the current state of the parameter of this
     *     fragment
     */
    private Field getResultingField() {
        if (isPreFilled) return Field.preFilledField(preFilledText);
        if (isText) return Field.textField(getHint(), NO_LIMIT);
        else return Field.numericField(isDecimal, isDecimal, getHint(), NO_LIMIT);
    }

    /**
     * Update the layout in function of the current state of the fragment, the update concern: - the
     * hint in the preview - if the preview is usable - the visibility of the decimal and sign
     * checkbox - minimum value of the limitPicker
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
        decimalCheckbox.setVisibility(checkBoxVisibility);
        signCheckbox.setVisibility(checkBoxVisibility);
    }

    /** @return true if the field want a number answer, False otherwise */
    private boolean isNumber() {
        return !isText && !isPreFilled;
    }

    /**
     * @return the String hint of the text view of this fragment regarding the type of this field
     */
    private String getHint() {
        if (isNumber()) {
            String hint = "0";
            if (isSigned && isDecimal) hint = "±0.0";
            if (isDecimal) hint = "0.0";
            if (isSigned) hint = "±0";
            return hint;
        } else {
            return isPreFilled ? getString(R.string.hint_pre_filled_field) : "???";
        }
    }
}
