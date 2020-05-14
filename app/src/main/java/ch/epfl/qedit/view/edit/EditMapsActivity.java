package ch.epfl.qedit.view.edit;

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
    public static final String LATITUDE = "ch.epfl.qedit.view.edit.LATITUDE";
    public static final String LONGITUDE = "ch.epfl.qedit.view.edit.LONGITUDE";

    private GoogleMap mMap;
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
        // TODO in future PR: get current location
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
        mMap = googleMap;

        // Add a marker on your current position and move the camera
        mMap.addMarker(
                new MarkerOptions().position(latLng).title(getString(R.string.your_location)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        CameraUpdate latLngLocation = CameraUpdateFactory.newLatLngZoom(latLng, 12);
        mMap.animateCamera(latLngLocation);

        mMap.setOnMapClickListener(
                point -> {
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(point));
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO in future PR: Add a new item to go back to current location
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        // TODO in future PR: implement menu options
        switch (id) {
            case R.id.next:
            case R.id.previous:
                break;
            case R.id.overview:
                break;
            case R.id.done:
                break;
        }

        return true;
    }
}
