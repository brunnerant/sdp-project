package ch.epfl.qedit.view.answer;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;

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

    private final ArrayList<TableRow> tableRow = new ArrayList<>();
    private final ArrayList<ArrayList<EditText>> arrayButtons = new ArrayList<>();
    private final ArrayList<ArrayList<Integer>> arrayIds = new ArrayList<>();

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.answers_table, container, false);

        TableLayout tableLayout = view.findViewById(R.id.answersTable);
        quizViewModel = new ViewModelProvider(requireActivity()).get(QuizViewModel.class);

        requireActivity()
                .getWindow()
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Initialize EditTexts
        for (int i = 0; i < answerFormat.getTableRowsNumber(); ++i) {
            TableRow t = new TableRow(requireActivity());
            arrayButtons.add(new ArrayList<EditText>());
            arrayIds.add(new ArrayList<Integer>());
            tableRow.add(t);
            for (int j = 0; j < answerFormat.getTableColumnsNumber(); ++j) {
                EditText editText = newEditText(i);

                // Get the entries stored in the model
                editText.setText(answerModel.getAnswer(i, j));
                addTextWatcher(editText, i, j);
                arrayButtons.get(i).add(editText);
                tableRow.get(i).addView(editText);
            }
            tableLayout.addView(tableRow.get(i));
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

    private EditText newEditText(int row) {
        EditText editText = new EditText(requireActivity());
        editText.setRawInputType(
                InputType.TYPE_CLASS_NUMBER
                        | InputType.TYPE_NUMBER_FLAG_DECIMAL
                        | InputType.TYPE_NUMBER_FLAG_SIGNED);
        editText.setKeyListener(
                DigitsKeyListener.getInstance(answerFormat.hasSign(), answerFormat.hasDecimal()));
        editText.setHint(answerFormat.getHint());

        editText.setFilters(
                new InputFilter[] {new InputFilter.LengthFilter(answerFormat.getMaxCharacters())});

        int id = View.generateViewId();
        arrayIds.get(row).add(id);
        editText.setId(id);

        return editText;
    }
}
