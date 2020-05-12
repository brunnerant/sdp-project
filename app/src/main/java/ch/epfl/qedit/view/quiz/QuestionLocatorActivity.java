package ch.epfl.qedit.view.quiz;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import ch.epfl.qedit.R;
import ch.epfl.qedit.backend.location.LocServiceFactory;
import ch.epfl.qedit.backend.location.LocationService;
import ch.epfl.qedit.util.LocaleHelper;

/**
 * This activity handles the navigation to the next question in treasure hunt quizzes. Because
 * we found it nicer, we don't show the location of the next question, but rather the
 * direction and distance, so that it gamifies the process.
 */
public class QuestionLocatorActivity extends AppCompatActivity implements LocationListener {

    public static final String QUESTION_LOCATION = "ch.epfl.qedit.view.quiz.QUESTION_LOCATION";
    private static final int REQUEST_CODE = 0;
    private static final String[] REQUESTED_PERMISSIONS = new String[] {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_NETWORK_STATE,
    };

    // This is the interval at which we would like to receive location updates
    private static final int LOCATION_INTERVAL = 1000;

    // This is the location of the question the user has to go to
    private Location questionLoc;

    // This is the location service from which we retrieve the phone's location
    private LocationService locService;

    // Those are the text views that display the direction and distance to the user
    private TextView distanceView;
    private TextView bearingView;

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
//        Bundle bundle = Objects.requireNonNull(getIntent().getExtras());
//        questionLoc = (Location) bundle.getSerializable(QUESTION_LOCATION);
        questionLoc = new Location("");
        questionLoc.setLongitude(0);
        questionLoc.setLatitude(0);

        // We retrieve the two text views
        distanceView = findViewById(R.id.question_distance);
        bearingView = findViewById(R.id.question_bearing);

        // We subscribe to the location service through the factory
        locService = LocServiceFactory.getInstance(getApplicationContext());

        // If we couldn't subscribe, we need to ask for permissions for the location
        if (!locService.subscribe(this, LOCATION_INTERVAL))
            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, REQUEST_CODE);
    }

    private void askPermissions() {
        ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != REQUEST_CODE || permissions.length != REQUESTED_PERMISSIONS.length)
            return;

        for (int i = 0; i < REQUESTED_PERMISSIONS.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                // If the permissions were not granted, we just show an error UI to the user
                setErrorUI();
                return;
            }
        }

        locService.subscribe(this, LOCATION_INTERVAL);
    }

    // Sets an error UI so that the user knows why he cannot see the next question
    private void setErrorUI() {
        distanceView.setText(getString(R.string.question_locator_error));
        bearingView.setText("");
    }

    @Override
    // This method is called when the location service sends location information
    public void onLocationChanged(Location location) {
        Log.d("qedit", "location updated");
        float distance = location.distanceTo(questionLoc);
        float targetBearing = location.bearingTo(questionLoc);
        float currentBearing = location.getBearing();

        float direction = (targetBearing - currentBearing) % 360;

        // This is used to have a number between 0 and 360
        if (direction < 0) direction += 360;

        updateUI(distance, targetBearing, direction);
    }

    // This method updates the UI when a new location measurement was made
    private void updateUI(float distance, float targetBearing, float direction) {
        distanceView.setText(String.format("%s %.0f", getString(R.string.question_locator_distance), distance));
        bearingView.setText(String.format("%s %.0f", getString(R.string.question_locator_bearing), targetBearing));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("qedit", "provider disabled: " + provider);
    }
}
