package stanford.cs194.stanfood;

import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

public class Database {
    final private String dbPath = "https://stanfood-e7255.firebaseio.com/";
    final private String stanfordLocSuffix = " Stanford, CA 94305";
    private FirebaseDatabase database;
    private DatabaseReference dbRef;

    public Database(){
        database = FirebaseDatabase.getInstance(dbPath);
        dbRef = database.getReference();
    }

    // createEntry: creates an entry in Firebase table of Object obj
    public void createEntry(String table, Object obj){
        DatabaseReference pushedTableRef = dbRef.child(table).push();
        pushedTableRef.setValue(obj);
    }

    // createUser: creates a new user in the users table
    public void createUser(String email, String name){
        createEntry("users", new User(email, name));
    }

    // createPin: creates a new pin in the pins table
    public void createPin(LatLng loc){
        createEntry("pins", new Pin(loc));
    }

    // createEvent: creates a new event in the events table
    // first searches to see if there is a pin at the associated location
    // if not, one is created. pinId is then retrieved, allowing the
    // event to be created
    public void createEvent(final String description, final String locationName, final LocalDateTime time){
        final LatLng loc = getLocationFromName(locationName);
        dbRef.child("pins").addListenerForSingleValueEvent(
            new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String pinId = null;
                    for(DataSnapshot ds : dataSnapshot.getChildren()){
                        double lat = ds.child("latitude").getValue(double.class);
                        double lng = ds.child("longitude").getValue(double.class);
                        LatLng l = new LatLng(lat, lng);
                        if(loc == l){
                            pinId = ds.getKey();
                            break;
                        }
                    }
                    if(pinId == null)
                        createPin(loc);
                    createEntry("events", new Event(pinId, description, locationName, time));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            }
        );
    }

    // createFood: creates a new food item in the food table
    public void createFood(String eventId, String description){
        createEntry("food", new Food(eventId, description));
    }

    // getLocationFromName: uses Geocoder package to return coordinates from a locationName
    private LatLng getLocationFromName(String locationName){
        Geocoder geo = new Geocoder(App.getContext(), Locale.US);
        String fullAddress = locationName + stanfordLocSuffix;
        LatLng loc = null;
        try {
            List<Address> addresses = geo.getFromLocationName(fullAddress, 1);
            if(addresses != null && !addresses.isEmpty()){
                Address address = addresses.get(0);
                loc = new LatLng(address.getLatitude(), address.getLongitude());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return loc;
    }
}
