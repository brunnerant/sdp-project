package ch.epfl.qedit.view.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import ch.epfl.qedit.R;
import ch.epfl.qedit.model.User;
import java.util.Objects;

public class HomeInfoFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_info, container, false);

        // Get user from the bundle created by the parent activity
        User user = (User) Objects.requireNonNull(getArguments()).getSerializable("user");

        String message = getResources().getString(R.string.welcome) + " " + Objects.requireNonNull(user).getFullName() + getResources().getString(R.string.exclamation_point);

        // Capture the layout's TextView and set the string as its text
        TextView textViewWelcome = view.findViewById(R.id.greeting);
        textViewWelcome.setText(message);

        TextView textViewRole = view.findViewById(R.id.role);

        textViewRole.setText(getRoleText(user.getRole()));

        return view;
    }

    private String getRoleText(User.Role role) {
        String roleText = "";
        switch (role) {
            case Administrator:
                roleText = getResources().getString(R.string.role_administrator);
                break;
            case Editor:
                roleText = getResources().getString(R.string.role_editor);
                break;
            case Participant:
                roleText = getResources().getString(R.string.role_participant);
                break;
            default:
                break;
        }

        return roleText;
    }
}
