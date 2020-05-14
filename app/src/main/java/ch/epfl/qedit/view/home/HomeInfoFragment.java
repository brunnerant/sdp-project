package ch.epfl.qedit.view.home;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static ch.epfl.qedit.view.login.TokenLogInActivity.USER;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import ch.epfl.qedit.R;
import ch.epfl.qedit.model.User;
import ch.epfl.qedit.view.util.StatisticCardView;
import java.util.Objects;

public class HomeInfoFragment extends Fragment {

    private User user;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_info, container, false);

        // Get user from the bundle created by the parent activity
        user = (User) Objects.requireNonNull(getArguments()).getSerializable(USER);

        String message =
                getResources().getString(R.string.welcome)
                        + " "
                        + Objects.requireNonNull(user).getFullName()
                        + getResources().getString(R.string.exclamation_point);

        // Capture the layout's TextView and set the string as its text
        TextView textViewWelcome = view.findViewById(R.id.greeting);
        textViewWelcome.setText(message);

        // set little red text in the top left of the statistics panel that allow the user to hide
        // the statistics panel or display it again
        HorizontalScrollView stats = view.findViewById(R.id.stats);
        TextView displayStats = view.findViewById(R.id.display_stats);
        displayStats.setOnClickListener(
                v -> {
                    boolean isHidden = stats.getVisibility() == GONE;
                    int textId = isHidden ? R.string.hide_stats : R.string.display_stats;
                    int visibility = isHidden ? VISIBLE : GONE;
                    displayStats.setText(textId);
                    stats.setVisibility(visibility);
                });

        setStatsData(view);

        return view;
    }

    /** Display the actual score, successes and attempts number of the current user */
    private void setStatsData(View view) {
        StatisticCardView scoreCard = view.findViewById(R.id.score_card);
        scoreCard.setData(user.getScore());
        StatisticCardView successesCard = view.findViewById(R.id.successes_card);
        successesCard.setData(user.getSuccesses());
        StatisticCardView attemptsCard = view.findViewById(R.id.attempts_card);
        attemptsCard.setData(user.getAttempts());
    }
}
