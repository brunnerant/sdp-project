package ch.epfl.qedit.view.answer;

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
import ch.epfl.qedit.R;
import ch.epfl.qedit.model.MatrixFormat;
import java.util.ArrayList;

public class MatrixFragment extends Fragment {
    private TableLayout tableLayout;
    private MatrixFormat matrixFormat;

    private ArrayList<TableRow> tableRow = new ArrayList<>();
    private ArrayList<ArrayList<EditText>> arrayButtons = new ArrayList<>();
    private ArrayList<ArrayList<Integer>> arrayIds = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        matrixFormat = (MatrixFormat) getArguments().getSerializable("m0");
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.answers_table, container, false);

        tableLayout = view.findViewById(R.id.answersTable);

        requireActivity()
                .getWindow()
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        for (int i = 0; i < matrixFormat.getTableRowsNumber(); ++i) {
            TableRow t = new TableRow(requireActivity());
            arrayButtons.add(new ArrayList<EditText>());
            arrayIds.add(new ArrayList<Integer>());
            tableRow.add(t);
            for (int j = 0; j < matrixFormat.getTableColumnsNumber(); ++j) {
                EditText editText = newEditText(i);
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

    private EditText newEditText(int row) {
        EditText editText = new EditText(requireActivity());
        editText.setRawInputType(
                InputType.TYPE_CLASS_NUMBER
                        | InputType.TYPE_NUMBER_FLAG_DECIMAL
                        | InputType.TYPE_NUMBER_FLAG_SIGNED);
        editText.setKeyListener(
                DigitsKeyListener.getInstance(matrixFormat.hasSign(), matrixFormat.hasDecimal()));
        editText.setHint(matrixFormat.getHint());

        editText.setFilters(
                new InputFilter[] {new InputFilter.LengthFilter(matrixFormat.getMaxCharacters())});

        int id = View.generateViewId();
        arrayIds.get(row).add(id);
        editText.setId(id);

        return editText;
    }
}
