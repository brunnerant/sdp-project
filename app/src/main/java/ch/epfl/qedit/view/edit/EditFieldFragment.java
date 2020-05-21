package ch.epfl.qedit.view.edit;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import ch.epfl.qedit.R;
import ch.epfl.qedit.model.answer.MatrixFormat;
import ch.epfl.qedit.model.answer.MatrixFormat.Field;
import ch.epfl.qedit.model.answer.MatrixModel;
import java.util.Objects;

public class EditFieldFragment extends DialogFragment {

    /** id of the boolean argument passed through a bundle at creation of a EditFieldCreation */
    private static final String IS_TEXT_ARG = "ch.epfl.qedit.view.edit.IS_TEXT_ARG";

    /** State variables, we determine the correct type of Field to create from this variable */
    private boolean isSigned;

    private boolean isDecimal;
    private boolean isText;
    private boolean isPreFilled;

    private String solution;

    /** Layout component */
    private CheckBox decimalCheckbox;

    private CheckBox signCheckbox;

    private EditText solutionView;
    private TextView preview;
    private Spinner typesSpinner;

    /** Index in the spinner of the different types */
    public static final int NUMBER_TYPE_IDX = 0;

    public static final int TEXT_TYPE_IDX = 1;
    public static final int PRE_FILLED_TYPE_IDX = 2;

    /** @return true if the field wants a number answer, False otherwise */
    private boolean isNumber() {
        return !isText && !isPreFilled;
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

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Get isText parameter from the bundle set in the newInstance function
        isText = Objects.requireNonNull(getArguments()).getBoolean(IS_TEXT_ARG);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // inflate custom layout
        LayoutInflater inflater = Objects.requireNonNull(getActivity()).getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_edit_field, null);

        initLayoutComponent(view);

        // set title red. Because it's nice.
        Spanned title =
                Html.fromHtml(
                        "<font color='#FF0000'>"
                                + getString(R.string.edit_field_title)
                                + "</font>");
        builder.setView(view)
                .setTitle(title)
                .setCancelable(true)
                .setPositiveButton(R.string.done, null)
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        doneListener(dialog);
        // Create the AlertDialog object and return it
        return dialog;
    }

    // HELPER METHODS THAT HANDLE THE RESULT OF THIS DIALOG //

    /** return Field resulting of the current state of the parameter of this fragment */
    private Field result() {
        if (isPreFilled) return Field.preFilledField(solution);
        else if (isText) return Field.textField(getHint());
        else return Field.numericField(isDecimal, isDecimal, getHint());
    }

    /** Return result of this dialog in the callee activity if solution is non-empty */
    private void returnResult() {
        if (solution == null || solution.isEmpty()) {
            solutionView.setError(getString(R.string.cannot_be_empty));
        } else {
            // Check if the parent is a EditQuestionActivity, if yes, use the setAnswerFormat of the
            // parent
            FragmentActivity parent = requireActivity();
            if (parent instanceof EditQuestionActivity) {
                MatrixModel solutionModel = new MatrixModel(1, 1);
                solutionModel.updateAnswer(0, 0, solution);
                MatrixFormat solutionAnswerFormat = MatrixFormat.singleField(result());
                solutionAnswerFormat.setCorrectAnswer(solutionModel);
                ((EditQuestionActivity) parent).setAnswerFormat(solutionAnswerFormat);
            }
            dismiss();
        }
    }

    // HELPER METHODS THAT CREATE LISTENERS //

    /**
     * We define the done listener in this weird way because otherwise, the click on the 'done'
     * button dismiss automatically the dialog and we don't want that (i.e. if the solution is
     * empty)
     */
    private void doneListener(final AlertDialog dialog) {
        dialog.setOnShowListener(
                dialogInterface -> {
                    Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    button.setOnClickListener(v -> returnResult());
                });
    }

    private void setSolutionViewListener() {
        solutionView.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(
                            CharSequence s, int start, int count, int after) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        solution = s.toString();
                    }

                    @Override
                    public void afterTextChanged(Editable s) {}
                });
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

    // METHODS THAT HANDLE THE LAYOUT //

    private void setCheckBoxesOnClickListener() {
        decimalCheckbox.setOnClickListener(
                v -> {
                    isDecimal = ((CheckBox) v).isChecked();
                    updateLayout();
                });
        signCheckbox.setOnClickListener(
                v -> {
                    isSigned = ((CheckBox) v).isChecked();
                    updateLayout();
                });
    }

    /**
     * Init all the layout component with listener and default parameters
     *
     * @param view View from which we retrieve layout component
     */
    private void initLayoutComponent(View view) {

        // find layout component in the layout
        decimalCheckbox = view.findViewById(R.id.decimal_checkbox);
        signCheckbox = view.findViewById(R.id.sign_checkbox);

        solutionView = view.findViewById(R.id.field_solution);
        preview = view.findViewById(R.id.field_hint_preview);
        typesSpinner = view.findViewById(R.id.field_types_selection);

        // create listeners for each check box
        setCheckBoxesOnClickListener();

        setTypesSpinnerListener();
        setSolutionViewListener();

        // set spinner to initial value
        int selectSpinner =
                isNumber() ? NUMBER_TYPE_IDX : isPreFilled ? PRE_FILLED_TYPE_IDX : TEXT_TYPE_IDX;
        typesSpinner.setSelection(selectSpinner);

        updateLayout();
    }

    /**
     * Update the layout in function of the current state of the fragment, the update concern: - the
     * hint in the preview - if the preview is usable - the visibility of the decimal and sign
     * checkbox - minimum value of the limitPicker
     */
    private void updateLayout() {

        // update solution hint
        int hintSolution = isPreFilled ? R.string.enter_pre_filled : R.string.enter_solution;
        solutionView.setHint(hintSolution);
        solutionView.setInputType(getInputType());
        solutionView.setText("");

        Spanned previewHint =
                Html.fromHtml("<b>" + getString(R.string.hint_preview) + ": </b>" + getHint());
        preview.setText(previewHint);

        // update checkbox visibility
        int checkBoxVisibility = isNumber() ? View.VISIBLE : View.GONE;
        decimalCheckbox.setVisibility(checkBoxVisibility);
        signCheckbox.setVisibility(checkBoxVisibility);
    }

    /** return the input type of the solutionView EditText */
    private int getInputType() {
        int type = InputType.TYPE_CLASS_NUMBER;
        if (isDecimal) type |= InputType.TYPE_NUMBER_FLAG_DECIMAL;
        if (isSigned) type |= InputType.TYPE_NUMBER_FLAG_SIGNED;
        return isNumber() ? type : InputType.TYPE_CLASS_TEXT;
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
        } else {
            return "???";
        }
    }
}
