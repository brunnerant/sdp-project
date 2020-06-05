package ch.epfl.qedit.view.answer;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import ch.epfl.qedit.R;
import ch.epfl.qedit.model.answer.MatrixFormat;
import ch.epfl.qedit.model.answer.MatrixModel;
import ch.epfl.qedit.viewmodel.QuizViewModel;
import java.util.ArrayList;

public class MatrixFragment extends AnswerFragment<MatrixFormat, MatrixModel> {
    private QuizViewModel quizViewModel;

    // This is used for debugging purposes, to retrieve the ids of the fields
    private ArrayList<ArrayList<Integer>> fieldIds;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.answer_table, container, false);
        quizViewModel = new ViewModelProvider(requireActivity()).get(QuizViewModel.class);

        fillTable(view.findViewById(R.id.answer_table));

        return view;
    }

    /** This is used for test purposes, so that individual fields can be retrieved */
    public int getId(int row, int column) {
        return fieldIds.get(row).get(column);
    }

    /** This method is used to fill the the given table with the correct fields */
    private void fillTable(TableLayout tableLayout) {
        fieldIds = new ArrayList<>();

        // This allows to chain the fields together, so that the user can edit all
        // of them without closing the soft keyboard
        EditText prevEditText = null;

        for (int i = 0; i < answerFormat.getNumRows(); i++) {
            TableRow row = new TableRow(requireActivity());
            fieldIds.add(new ArrayList<>());

            for (int j = 0; j < answerFormat.getNumColumns(); j++) {
                int id = View.generateViewId();
                MatrixFormat.Field field = answerFormat.getField(i, j);
                View fieldView;

                if (field.getType() == MatrixFormat.Field.Type.PreFilled) {
                    fieldView = createPreFilledField(field);
                } else {
                    if (prevEditText != null) {
                        // We link the previous editable field with this one
                        prevEditText.setNextFocusForwardId(id);
                        prevEditText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                    }

                    prevEditText = createEditableField(field, answerModel.getAnswer(i, j));
                    addTextWatcher(prevEditText, i, j);
                    fieldView = prevEditText;
                }

                fieldIds.get(i).add(id);
                fieldView.setId(id);
                row.addView(fieldView);
            }

            tableLayout.addView(row);
        }

        // When the user visits the last field, he probably wants to close the soft keyboard
        if (prevEditText != null) prevEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
    }

    /** This is used to create a field which contains non-editable text */
    private TextView createPreFilledField(MatrixFormat.Field field) {
        TextView view = new TextView(requireActivity());
        view.setText(field.getText());
        view.setTextAppearance(requireActivity(), R.style.Widget_AppCompat_EditText);
        view.setGravity(Gravity.CENTER);
        return view;
    }

    /** This is used to create a field that can be edited with text or numbers */
    private EditText createEditableField(MatrixFormat.Field field, String answer) {
        MatrixFormat.Field.Type type = field.getType();

        EditText editText = new EditText(requireActivity());
        int inputType =
                type == MatrixFormat.Field.Type.Text
                        ? InputType.TYPE_CLASS_TEXT
                        : InputType.TYPE_CLASS_NUMBER;

        if (type.isDecimal()) inputType |= InputType.TYPE_NUMBER_FLAG_DECIMAL;
        if (type.isSigned()) inputType |= InputType.TYPE_NUMBER_FLAG_SIGNED;

        editText.setInputType(inputType);
        editText.setText(answer);
        editText.setHint(field.getText());
        return editText;
    }

    /** This registers the text watchers that allow to store the answers */
    private void addTextWatcher(final EditText editText, final int row, final int col) {
        editText.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(
                            CharSequence s, int start, int count, int after) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {}

                    @Override
                    public void afterTextChanged(Editable s) {
                        // Update the value in the model
                        answerModel.updateAnswer(row, col, editText.getText().toString());
                    }
                });
    }
}
