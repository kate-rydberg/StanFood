package stanford.cs194.stanfood.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import stanford.cs194.stanfood.R;
import stanford.cs194.stanfood.authentication.Authentication;
import stanford.cs194.stanfood.database.CreateList;
import stanford.cs194.stanfood.database.Database;
import stanford.cs194.stanfood.fragments.BottomSheet;
import stanford.cs194.stanfood.fragments.BottomSheetListView;
import stanford.cs194.stanfood.fragments.NavigationDrawer;
import stanford.cs194.stanfood.fragments.PopUpFragment;
import stanford.cs194.stanfood.helpers.FirebaseInstanceIdAccessor;
import stanford.cs194.stanfood.models.Pin;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, OnMarkerClickListener, GoogleMap.OnMapClickListener, GoogleMap.OnCameraMoveStartedListener {
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private BottomSheet bottomSheet;
    private NavigationDrawer drawerLayout;
    private HashMap<LatLng,String> eventStorage;
    private HashMap<LatLng,Marker> markerStorage;

    private FusedLocationProviderClient mFusedLocationClient;
    private float distanceRange = 10000;
    private Authentication auth;
    private Database db;
    private FirebaseInstanceIdAccessor instanceIdAccessor;
    private FragmentManager supportFragment;
    private String clickedPinId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        supportFragment = getSupportFragmentManager();

        auth = new Authentication();
        db = new Database();
        instanceIdAccessor = new FirebaseInstanceIdAccessor(db, auth);
        instanceIdAccessor.uploadInstanceId();

        eventStorage = new HashMap<>();
        markerStorage = new HashMap<>();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Checks null to account for case that onResume called before drawerLayout initialized
        if (drawerLayout != null) {
            drawerLayout.setAuthenticationMenuOptions();
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //adds location marker
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.styledmap));

            if (!success) {
                Log.e("styled_map", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("styled_map", "Can't find style. Error: ", e);
        }
        enableMyLocation();

        loadPreviousIntent();

        if (clickedPinId != null) { // Center on a given pin
            db.dbRef.child("pins").child(clickedPinId).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot){
                            Pin pin = dataSnapshot.getValue(Pin.class);
                            LatLng coordinate = pin.getLocationCoordinate();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinate,16));
                            Location location = new Location(LocationManager.GPS_PROVIDER);
                            location.setLatitude(coordinate.latitude);
                            location.setLongitude(coordinate.longitude);
                            populatePins(location);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e("ERROR",databaseError.toString());
                        }
                    }
            );
        } else { // Center on user's current or last known location
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        LatLng current = new LatLng(location.getLatitude(),location.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current,16));
                        populatePins(location);
                    }
                }
            });
        }

        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(this);
        mMap.setOnCameraMoveStartedListener(this);

        // Get the bottom sheet view
        View bottomSheetView = findViewById(R.id.bottom_sheet);
        bottomSheet = new BottomSheet(bottomSheetView.getContext(), bottomSheetView, mMap);
        bottomSheet.moveListener();
        // Set padding to show Google logo in correct position
        mMap.setPadding(0, 0, 0, (int)bottomSheet.getPeekHeight());

        setupNavigationMenu();
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

        bottomSheet.initExpandedHeight();
        bottomSheet.expand();
        mMap.setPadding(0, 0, 0, (int)bottomSheet.getExpandedHeight());
        mMap.animateCamera(CameraUpdateFactory.newLatLng(location),500,null);

        BottomSheetListView eventListView = findViewById(R.id.eventList);
        ViewGroup bottomSheetContents = findViewById(R.id.bottom_sheet_contents);
        ViewCompat.setNestedScrollingEnabled(eventListView, true);

        CreateList initRows = new CreateList(db, eventListView, bottomSheetContents, supportFragment);
        initRows.createLocationEventList(marker, eventStorage);

        return true;
    }

    /**
     * Listen for when map is clicked and hide bottom sheet if it is expanded.
     * @param latLng - location of click
     */
    @Override
    public void onMapClick(LatLng latLng) {
        if (!bottomSheet.isCollapsed()) {
            bottomSheet.collapse();
        }
    }

    /**
     * Listen for when camera starts moving and collapse bottom sheet.
     * Only want to collapse bottom sheet when user drags the map. Ignore marker clicks.
     * @param i - reason the camera motion started
     */
    @Override
    public void onCameraMoveStarted(int i) {
        if (bottomSheet.isExpanded()) {
            if (i == REASON_GESTURE) {
                mMap.setPadding(0, 0, 0, (int)bottomSheet.getPeekHeight());
                bottomSheet.collapse();
            }
        }
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

    /**
     * Loads the previous intent being passed in and opens a pop up fragment if the intent contains
     * event details from a push notification being tapped
     */
    private void loadPreviousIntent() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            clickedPinId = extras.getString("clickedPinId");
            String clickedEventName = extras.getString("clickedEventName");
            String clickedLocationName = extras.getString("clickedLocationName");
            String clickedTimeRange = extras.getString("clickedTimeRange");
            String clickedEventDescription = extras.getString("clickedEventDescription");

            PopUpFragment.newInstance(clickedEventName, clickedLocationName, clickedTimeRange, clickedEventDescription)
                    .show(supportFragment, null);
        }
    }

    /**
     * Populates the map with all pins within a distance range of the current location of
     * the user by reading the Firebase database and comparing
     * the current location with the location of every pin.
     * If a pin has no events associated with it, then it disappears from the map.
     */
    public void populatePins(final Location cur){
        db.dbRef.child("pins").addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds : dataSnapshot.getChildren()){
                            if(ds.hasChildren()){
                                Pin curPin = ds.getValue(Pin.class);
                                LatLng coordinate = curPin.getLocationCoordinate();
                                Location loc = new Location(LocationManager.GPS_PROVIDER);
                                loc.setLatitude(coordinate.latitude);
                                loc.setLongitude(coordinate.longitude);
                                if(cur.distanceTo(loc) < distanceRange &&
                                        !eventStorage.containsKey(coordinate)){
                                    Marker m = mMap.addMarker(new MarkerOptions().position(coordinate));
                                    markerStorage.put(coordinate, m);
                                    eventStorage.put(coordinate, ds.getKey());
                                }
                                if(curPin.getNumEvents() == 0){
                                    Marker m = markerStorage.get(coordinate);
                                    if(m != null) {
                                        m.remove();
                                        markerStorage.remove(coordinate);
                                        eventStorage.remove(coordinate);
                                    }
                                }
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("ERROR",databaseError.toString());
                    }
                }
        );
    }

    /**
     * Creates the drawer layout and adds listeners.
     */
    private void setupNavigationMenu() {
        DrawerLayout mDrawerLayout = findViewById(R.id.drawer_layout);
        final NavigationView navigationView = findViewById(R.id.nav_view);
        drawerLayout = new NavigationDrawer(this, mDrawerLayout, navigationView, instanceIdAccessor);
        drawerLayout.addMenuIconListener();
        drawerLayout.addNavigationListener();
        drawerLayout.setAuthenticationMenuOptions();
        moveCompassPosition();
        createNavigationMenuListener();
    }

    /**
     * Adds a listener so that when the hamburger menu icon is clicked,
     * the navigation menu opens.
     */
    private void createNavigationMenuListener() {
        View menu_view = findViewById(R.id.hamburger_menu);
        menu_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer();
            }
        });
    }

    /**
     * Moves the compass position down, so that the hamburger menu does not cover it.
     */
    private void moveCompassPosition() {
        View compassButton = mapFragment.getView().findViewWithTag("GoogleMapCompass");
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) compassButton.getLayoutParams();
        rlp.addRule(RelativeLayout.ALIGN_PARENT_START, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.rightMargin = rlp.leftMargin;
        rlp.bottomMargin = 25;
    }
}
