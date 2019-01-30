package stanford.cs194.stanfood;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
        Log.d("MapsActivity","Running");
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        LatLng home = new LatLng(37.4248955,-122.1768221);
        float zoom = 17;
        mMap.addMarker(new MarkerOptions().position(home).title("Lagunita Court").snippet("Dorm"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(home, zoom));
    }


    /**
     * Displays a list of pin markers on the map.
     *
     * @param googleMap
     * @param pins
     */
    private void displayMarkers(GoogleMap googleMap, List<Pin> pins) {
        for (Pin pin:pins) {
            LatLng coord = pin.locationCoordinate;
            mMap.addMarker(new MarkerOptions()
                            .position(coord)
                            .title(pin.locationName)
                            .snippet(pin.description));
        }
    }
}
