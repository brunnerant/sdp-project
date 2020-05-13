package ch.epfl.qedit.view.edit;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Objects;

import ch.epfl.qedit.R;
import ch.epfl.qedit.model.User;

import static ch.epfl.qedit.view.LoginActivity.USER;

public class EditMapsActivity extends FragmentActivity implements OnMapReadyCallback {
    public static final String LATITUDE = "ch.epfl.qedit.view.edit.LATITUDE";
    public static final String LONGITUDE = "ch.epfl.qedit.view.edit.LONGITUDE";
    private final String yourLocation = getString(R.string.your_location);

    private GoogleMap mMap;
    private LatLng latLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        double latitude = Objects.requireNonNull(intent.getExtras()).getDouble(LATITUDE);
        double longitude = Objects.requireNonNull(intent.getExtras()).getDouble(LONGITUDE);
        latLng = new LatLng(latitude, longitude);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        mMap.addMarker(new MarkerOptions().position(latLng).title(yourLocation));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        CameraUpdate latLngLocation = CameraUpdateFactory.newLatLngZoom(latLng, 12);
        mMap.animateCamera(latLngLocation);

        mMap.setOnMapClickListener(point -> {
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(point));
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.map_menu, menu);
        return true;
    }

}
