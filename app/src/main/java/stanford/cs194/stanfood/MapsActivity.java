package stanford.cs194.stanfood;

<<<<<<< HEAD
import android.support.design.widget.BottomSheetBehavior;
=======
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
>>>>>>> track current location
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, OnMarkerClickListener {

    final private String dbPath = "https://stanfood-e7255.firebaseio.com/";
    private GoogleMap mMap;
<<<<<<< HEAD
    private View bottomSheet;
    private BottomSheetBehavior<View> mBottomSheetBehavior;
    private Map<String, String> markers = new HashMap<>();
    private FirebaseDatabase database;
    private DatabaseReference dbRef;
=======
    private DatabaseReference database;
<<<<<<< HEAD
>>>>>>> track current location
=======
    private FusedLocationProviderClient mFusedLocationClient;

>>>>>>> current location tracking

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
<<<<<<< HEAD

        bottomSheet = findViewById( R.id.bottom_sheet );
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setHideable(true);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        database = FirebaseDatabase.getInstance(dbPath);
        dbRef = database.getReference();
=======
        database = FirebaseDatabase.getInstance().getReference();
>>>>>>> track current location
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
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("MapsActivity","Running");
        mMap = googleMap;
        //adds location marker
        enableMyLocation();


        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);

        LatLng home = new LatLng(37.4248955,-122.1768221);
        float zoom = 17;
        mMap.addMarker(new MarkerOptions().position(home).title("Lagunita Court").snippet("Dorm"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(home, zoom));

        googleMap.setOnMarkerClickListener(this);

        // TEST: delete later
        Pin pin = new Pin(new LatLng(37.4243048,-122.1730309));
        List<Pin> pins = new ArrayList<>();
        pins.add(pin);
        displayMarkers(pins);
    }


    /**
     * Displays a list of pin markers on the map.
     *
     * @param pins - list of Pin objects
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


    /**
     * Expand bottom info window when a pin is clicked.
     *
     * @param marker - the pin that is clicked
     * @return - true to indicate the action was successful
     */
    @Override
    public boolean onMarkerClick(Marker marker) {
        LatLng location = marker.getPosition();
        String pinId = markers.get(marker.getId());
        // TODO: get text description or list of events to display
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        mBottomSheetBehavior.setHideable(false);

        // center the marker in the map area above the bottom sheet
        mMap.setPadding(0, 0, 0, 1000);
        mMap.animateCamera(CameraUpdateFactory.newLatLng(location));
        return true;

        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        //mMap.moveCamera(CameraUpdateFactory.zoomIn());

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            LatLng current = new LatLng(location.getLatitude(),location.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current,16));
                        }
                    }
                });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // Check if location permissions are granted and if so enable the
        // location data layer.
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0
                        && grantResults[0]
                        == PackageManager.PERMISSION_GRANTED) {
                    enableMyLocation();
                    break;
                }
        }
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }
    }


}
