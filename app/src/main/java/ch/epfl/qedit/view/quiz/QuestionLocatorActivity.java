package ch.epfl.qedit.view.quiz;

import android.Manifest;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import ch.epfl.qedit.R;
import ch.epfl.qedit.backend.location.LocServiceFactory;
import ch.epfl.qedit.backend.location.LocationService;
import ch.epfl.qedit.backend.permission.PermManagerFactory;
import ch.epfl.qedit.backend.permission.PermissionActivity;
import ch.epfl.qedit.backend.permission.PermissionManager;
import ch.epfl.qedit.util.LocaleHelper;
import java.util.Objects;

/**
 * This activity handles the navigation to the next question in treasure hunt quizzes. Because we
 * found it nicer, we don't show the location of the next question, but rather the direction and
 * distance, so that it gamifies the process.
 */
public class QuestionLocatorActivity extends PermissionActivity
        implements LocationListener, PermissionManager.OnPermissionResult {

    // Those are the keys of the arguments passed with the bundle to this activity
    public static final String QUESTION_LOCATION = "ch.epfl.qedit.view.quiz.QUESTION_LOCATION";
    public static final String QUESTION_RADIUS = "ch.epfl.qedit.view.quiz.QUESTION_RADIUS";

    // Those are the permissions needed by this activity
    private static final int REQUEST_CODE = 0;
    private static final String[] REQUESTED_PERMISSIONS =
            new String[] {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            };

    // This is the interval at which we would like to receive location updates
    private static final int LOCATION_INTERVAL = 1000;

    // This is the location and radius of the question the user has to go to
    private Location questionLoc;
    private float questionRadius;

    // This is the location service from which we retrieve the phone's location, and the
    // location manager from which we request permissions
    private LocationService locService;
    private PermissionManager permManager;

    // Those are the text views that display the direction and distance to the user
    private TextView text1;
    private TextView text2;

    // This button has two usages: allowing the user to answer the question, and allowing him
    // to grant the location permission
    private Button button;

    public QuestionLocatorActivity() {}

    @Override
    /* This method is needed to apply the desired language at the activity startup */
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_locator);

        // We retrieve the location from the activity arguments
        Bundle bundle = Objects.requireNonNull(getIntent().getExtras());
        questionLoc = (Location) bundle.getParcelable(QUESTION_LOCATION);
        questionRadius = bundle.getFloat(QUESTION_RADIUS);

        // We retrieve the UI elements and set up the default UI
        text1 = findViewById(R.id.question_locator_text1);
        text2 = findViewById(R.id.question_locator_text2);
        button = findViewById(R.id.question_locator_button);
        setUnknownUI();

        // We retrieve the two singleton backend services
        locService = LocServiceFactory.getInstance(getApplicationContext());
        permManager = PermManagerFactory.getInstance();

        // If we couldn't subscribe, we need to ask for permissions for the location
        if (!locService.subscribe(this, LOCATION_INTERVAL)) askPermissions();
    }

    private void askPermissions() {
        permManager.requestPermissions(this, this, REQUESTED_PERMISSIONS);
    }

    @Override
    public void onPermissionResult(String[] permissions, boolean[] granted) {
        for (int i = 0; i < granted.length; i++) {
            if (!granted[i]) {
                // If the permissions were not granted, we just show an error UI to the user
                // If it is the first time the user denies, we show the button, otherwise we
                // just give up and show nothing.
                if (permManager.shouldAskAgain(this, permissions[i])) setPermissionUI();
                else button.setVisibility(View.GONE);

                return;
            }
        }

        // The user finally accepted, so we can hide the button and subscribe to the location
        locService.subscribe(this, LOCATION_INTERVAL);
        button.setVisibility(View.GONE);
    }

    // Sets the text of the UI to inform the user that his position is unknown
    private void setUnknownUI() {
        text1.setText(getString(R.string.question_locator_error));
        text2.setText("");
    }

    // Sets the button of the UI so that the user can allow the location
    private void setPermissionUI() {
        button.setOnClickListener(v -> askPermissions());
        button.setText(R.string.enable_location);
        button.setVisibility(View.VISIBLE);
    }

    // This method shows the final UI that appears when the user found the question
    private void setFinishUI() {
        text1.setText(getString(R.string.question_locator_found));
        text2.setText("");
        button.setOnClickListener(
                v -> Toast.makeText(this, "Not yet implemented", Toast.LENGTH_SHORT).show());
        button.setText(getString(R.string.question_locator_answer));
        button.setVisibility(View.VISIBLE);
    }

    // This method is used to prevent the modulo from returning negative numbers
    private static float mod(float a, float b) {
        float m = a % b;
        return m < 0 ? m + b : m;
    }

    @Override
    // This method is called when the location service sends location information
    public void onLocationChanged(Location location) {
        float distance = location.distanceTo(questionLoc);
        float targetBearing = location.bearingTo(questionLoc);
        float currentBearing = location.getBearing();

        updateUI(distance, mod(targetBearing, 360), mod(targetBearing - currentBearing, 360));
    }

    // This function is used to ensure that the modulo always return a positive number
    // This method returns a user friendly distance indication from a distance measured in meters
    private static String formatDistance(float distance) {
        // Note: \u00A0 is a non-breaking space
        if (distance < 1000) return String.format("%.0f\u00A0m", distance);
        else return String.format("%.0f\u00A0km", distance / 1000);
    }

    // This method updates the UI when a new location measurement was made
    private void updateUI(float distance, float targetBearing, float direction) {
        // If the user found the question, we can stop the location service
        if (distance < questionRadius) {
            locService.unsubscribe(this);
            setFinishUI();
            return;
        }

        text1.setText(
                String.format(
                        "%s %s",
                        getString(R.string.question_locator_distance), formatDistance(distance)));
        text2.setText(
                String.format(
                        "%s %.0f°", getString(R.string.question_locator_bearing), targetBearing));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onProviderDisabled(String provider) {}
}
