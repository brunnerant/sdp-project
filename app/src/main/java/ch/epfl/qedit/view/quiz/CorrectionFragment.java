package ch.epfl.qedit.view.quiz;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import ch.epfl.qedit.R;
import java.util.ArrayList;
import java.util.Collections;

/** A simple {@link Fragment} subclass. */
public class CorrectionFragment extends Fragment {

    private boolean success = false;
    private float nbOfGoodAnswers;
    private float quizSize;

    public CorrectionFragment() {}

    public CorrectionFragment(ArrayList<Integer> goodAnswers) {
        nbOfGoodAnswers = Collections.frequency(goodAnswers, 1);
        // success is true if at least 80 percent of asnwers are good
        quizSize = goodAnswers.size();
        success = (nbOfGoodAnswers / quizSize) >= 0.8 ? true : false;
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_correction, container, false);

        TextView score = view.findViewById(R.id.correction_score);
        TextView nbOfSuccesses = view.findViewById(R.id.correction_success);
        TextView ratio = view.findViewById(R.id.correct_ratio);

        String scoreText = "+" + (int) nbOfGoodAnswers;
        score.setText(scoreText);

        if (success) {
            nbOfSuccesses.setTextColor(Color.GREEN);
            nbOfSuccesses.setText("+1");
            ratio.setTextColor(Color.GREEN);
        } else {
            nbOfSuccesses.setTextColor(Color.RED);
            ratio.setTextColor(Color.RED);
            nbOfSuccesses.setText("+0");
        }
        String ratioText =
                getString(R.string.correct_answers)
                        + (int) nbOfGoodAnswers
                        + "/"
                        + (int) quizSize
                        + " ("
                        + (nbOfGoodAnswers / quizSize * 100)
                        + "%)";
        ratio.setText(ratioText);

        return view;
    }
}
