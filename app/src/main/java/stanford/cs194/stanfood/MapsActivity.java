package stanford.cs194.stanfood;

import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, OnMarkerClickListener {

    private GoogleMap mMap;
    private BottomSheetBehavior<View> mBottomSheetBehavior;
    private Map<String, String> markers = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        View bottomSheet = findViewById( R.id.bottom_sheet );
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setHideable(true);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
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

        googleMap.setOnMarkerClickListener(this);

        // TEST, delete later
        Pin pin = new Pin(new LatLng(37.4243048,-122.1730309));
        List<Pin> pins = new ArrayList<>();
        pins.add(pin);
        displayMarkers(pins);
    }


    /**
     * Displays a list of pin markers on the map.
     *
     * @param pins
//     */
    private void displayMarkers(List<Pin> pins) {
        for (Pin pin:pins) {
            LatLng coordinate = pin.getLocationCoordinate();
            Marker markerObj = mMap.addMarker(new MarkerOptions()
                            .position(coordinate)
                            .title(pin.getLocationName()));
            markers.put(markerObj.getId(), pin.getPinId());
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        String pinId = markers.get(marker.getId());
        if (mBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
        return true;
    }
}
