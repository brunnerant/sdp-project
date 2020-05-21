package ch.epfl.qedit.view.edit;

import static ch.epfl.qedit.view.edit.EditQuestionActivity.LATITUDE;
import static ch.epfl.qedit.view.edit.EditQuestionActivity.LONGITUDE;
import static ch.epfl.qedit.view.edit.EditQuestionActivity.MAP_REQUEST_CODE;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import ch.epfl.qedit.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class EditMapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap map;
    private LatLng latLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        // This below will be the default location if user refuse location permission
        double latitude = 46.518442;
        double longitude = 6.561983;
        latLng = new LatLng(latitude, longitude);
    }

    /**
     * Manipulates the map once available. This callback is triggered when the map is ready to be
     * used. This is where we can add markers or lines, add listeners or move the camera. In this
     * case, we just add a marker near Sydney, Australia. If Google Play services is not installed
     * on the device, the user will be prompted to install it inside the SupportMapFragment. This
     * method will only be triggered once the user has installed Google Play services and returned
     * to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        // Add a marker on your current position and move the camera
        map.addMarker(
                new MarkerOptions().position(latLng).title(getString(R.string.your_location)));
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        CameraUpdate latLngLocation = CameraUpdateFactory.newLatLngZoom(latLng, 12);
        map.animateCamera(latLngLocation);

        map.setOnMapClickListener(
                point -> {
                    map.clear();
                    map.addMarker(new MarkerOptions().position(point));
                    latLng = point;
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO in future PR: Add a new item to go back to current location
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        MenuItem next = menu.findItem(R.id.next);
        MenuItem previous = menu.findItem(R.id.previous);
        MenuItem overview = menu.findItem(R.id.overview);

        // We don't need below for the map
        next.setVisible(false);
        previous.setVisible(false);
        overview.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.done) {
            onBackPressed();
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(LONGITUDE, latLng.longitude);
        intent.putExtra(LATITUDE, latLng.latitude);

        setResult(MAP_REQUEST_CODE, intent);
        finish();
    }
}
