package ch.epfl.qedit.view.answer;

import android.content.Context;
import android.graphics.drawable.AnimatedStateListDrawable;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;

import ch.epfl.qedit.R;
import ch.epfl.qedit.model.MatrixFormat;
import ch.epfl.qedit.viewmodel.QuizViewModel;

public class MatrixFragment extends Fragment {
    private TableLayout tableLayout;
    private boolean hasDecimal = true;
    private boolean hasSign = true;

    private ArrayList<TableRow> tableRow = new ArrayList<>();
    private ArrayList<ArrayList<EditText>> arrayButtons = new ArrayList<>();;

    private int tableRowsNumber = 1;
    private int tableColumnsNumber = 1;
    private int maxCharacters = 3;
    private String hintString;
    private MatrixFormat matrixFormat;
    private Context context;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        matrixFormat = (MatrixFormat) getArguments().getSerializable("m0");
        setValues();
    }

    // Used to get the context
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        context = null;
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.answers_table, container, false);

        tableLayout = view.findViewById(R.id.answersTable);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        for(int i = 0; i < tableRowsNumber; ++i) {
            TableRow t = new TableRow(getActivity());
            arrayButtons.add(new ArrayList<EditText>());
            tableRow.add(t);
            for(int j = 0; j < tableColumnsNumber; ++j) {
                EditText editText = newEditText();
                arrayButtons.get(i).add(editText);
                tableRow.get(i).addView(editText);
            }
            tableLayout.addView(tableRow.get(i));
        }

        return view;
    }
    private void setValues() {
        hasDecimal = matrixFormat.getHasDecimal();
        hasSign = matrixFormat.getHasSign();
        hintString = matrixFormat.getHint();
        tableColumnsNumber = matrixFormat.getTableColumnsNumberNumber();
        tableRowsNumber = matrixFormat.getTableRowsNumber();
        maxCharacters = matrixFormat.getMaxCharacters();
    }

    private EditText newEditText() {
        EditText editText = new EditText(getActivity());
        editText.setRawInputType(
                InputType.TYPE_CLASS_NUMBER
                        | InputType.TYPE_NUMBER_FLAG_DECIMAL
                        | InputType.TYPE_NUMBER_FLAG_SIGNED);
        editText.setKeyListener(DigitsKeyListener.getInstance(hasSign, hasDecimal));
        editText.setHint(hintString);

        editText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxCharacters)});

        return editText;
    }
}
