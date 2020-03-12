package ch.epfl.qedit.view.question;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

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
        View view = inflater.inflate(R.layout.quiz_overview_fragment, container, false);

        // Create the menu
        String[] menuItems = {"A1",
        "A2",
        "A3"};

        ListView listView = (ListView) view.findViewById(R.id.questionList);

        ArrayAdapter<String> listViewAdapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                menuItems
        );

        listView.setAdapter(listViewAdapter);
        final FragmentTransaction ft = getFragmentManager().beginTransaction();

        // Reaction when we click
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            // parent: our listView
            // view: item within the list that we clicked on
            // position: index of the item that was clicked on
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(), "question " + position, Toast.LENGTH_SHORT).show();

                QuestionFragment qFrag = QuestionFragment.newInstance();
                ft.replace(R.id.questionFrame, qFrag);
            }
        });
        ft.commit();

        return view;
    }
}
