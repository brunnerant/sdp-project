package ch.epfl.qedit.view.question;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.fragment.app.Fragment;
import ch.epfl.qedit.R;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass. Use the {@link TableFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TableFragment extends Fragment {
    private int sizeX;
    private int sizeY;
    private List<View> answers;

    private Button buttonSubmit;

    public TableFragment() {
        // Required empty public constructor

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get size of the matrix
        sizeX = (int) getArguments().getSerializable("s0");
        sizeY = (int) getArguments().getSerializable("s1");

        // create an arraylist as big as the matrix
        answers = new ArrayList<>(sizeX * sizeY);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_table, container, false);

        // fragment numbers are not in order as they were put in the layout in a different order
        // still, 1st element of array is 1st element of matrix and last element of array is last
        // element of matrix

        return view;
    }
}
