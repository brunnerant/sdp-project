package ch.epfl.qedit.view.quiz;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import ch.epfl.qedit.backend.location.LocServiceFactory;
import ch.epfl.qedit.backend.location.LocationService;
import ch.epfl.qedit.util.LocaleHelper;
import java.util.Objects;

/**
 * This fragment handles the navigation to the next question in treasure hunt quizzes. Because the
 * Qedit team found it nicer, we don't show the location of the next question, but rather the
 * direction and distance, so that it gamifies the process.
 */
class QuestionLocatorActivity extends AppCompatActivity {

    public static final String QUESTION_LOCATION = "ch.epfl.qedit.view.quiz.QUESTION_LOCATION";

    // This is the location of the question the user has to go to
    private Location questionLoc;

    // This is the location service from which we retrieve the phone's location
    private LocationService locService;

    public QuestionLocatorActivity() {}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // We retrieve the location from the activity arguments
        Bundle bundle = Objects.requireNonNull(getIntent().getExtras());
        questionLoc = (Location) bundle.getSerializable(QUESTION_LOCATION);

        // We retrieve the location service using the factory
        locService = LocServiceFactory.getInstance(getApplicationContext());
    }

    @Override
    /* This method is needed to apply the desired language at the activity startup */
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }
}
