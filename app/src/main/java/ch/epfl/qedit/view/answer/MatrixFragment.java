package ch.epfl.qedit.view.answer;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.HashMap;

import ch.epfl.qedit.R;
import ch.epfl.qedit.model.answer.AnswerModel;
import ch.epfl.qedit.model.answer.MatrixFormat;
import ch.epfl.qedit.model.answer.MatrixModel;
import ch.epfl.qedit.viewmodel.QuizViewModel;

public class MatrixFragment extends AnswerFragment<MatrixFormat, MatrixModel> {
    private QuizViewModel quizViewModel;

    private ArrayList<ArrayList<Integer>> arrayIds;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.answer_table, container, false);
        TableLayout tableLayout = view.findViewById(R.id.answer_table);

        quizViewModel = new ViewModelProvider(requireActivity()).get(QuizViewModel.class);

        requireActivity()
                .getWindow()
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        arrayIds = new ArrayList<>();
        for (int i = 0; i < answerFormat.getNumRows(); i++) {
            TableRow row = new TableRow(requireActivity());
            arrayIds.add(new ArrayList<Integer>());

            for (int j = 0; j < answerFormat.getNumColumns(); j++) {
                View fieldView = createView(answerFormat.getField(j, i), answerModel.getAnswer(i, j), i, j);

                int id = View.generateViewId();
                arrayIds.get(i).add(id);
                fieldView.setId(id);

                row.addView(fieldView);
            }

            tableLayout.addView(row);
        }

        return view;
    }

    public int getId(int row, int column) {
        return arrayIds.get(row).get(column);
    }

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

                        // and update the AnswerModel stored in the QuizViewModel
                        HashMap<Integer, AnswerModel> map = quizViewModel.getAnswers().getValue();
                        map.remove(quizViewModel.getFocusedQuestion().getValue());
                        map.put(quizViewModel.getFocusedQuestion().getValue(), answerModel);
                    }
                });
    }

    private View createView(MatrixFormat.Field field, String answer, int row, int col) {
        MatrixFormat.Field.Type type = field.getType();

        if (type == MatrixFormat.Field.Type.PreFilled) {
            TextView view = new TextView(requireActivity());
            view.setText(field.getText());
            return view;
        }

        EditText editText = new EditText(requireActivity());
        int inputType = InputType.TYPE_CLASS_NUMBER;

        if (type == MatrixFormat.Field.Type.SignedFloat || type == MatrixFormat.Field.Type.UnsignedFloat)
            inputType |= InputType.TYPE_NUMBER_FLAG_DECIMAL;

        if (type == MatrixFormat.Field.Type.SignedFloat || type == MatrixFormat.Field.Type.SignedInt)
            inputType |= InputType.TYPE_NUMBER_FLAG_SIGNED;

        editText.setInputType(inputType);
        editText.setText(answer);
        editText.setFilters(new InputFilter[] {
                new InputFilter.LengthFilter(field.getMaxCharacters())
        });
        addTextWatcher(editText, row, col);

        return editText;
    }
}
