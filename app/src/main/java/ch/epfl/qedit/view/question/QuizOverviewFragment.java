package ch.epfl.qedit.view.question;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ch.epfl.qedit.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class QuizOverviewFragment extends Fragment {

    public QuizOverviewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.quiz_overview_fragment, container, false);
    }
}
