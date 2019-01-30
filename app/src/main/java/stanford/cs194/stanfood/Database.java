package stanford.cs194.stanfood;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Database {
    final private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbRef;
    final private String dbPath = "https://stanfood-e7255.firebaseio.com/";
    public Database(){
        dbRef = database.getReference(dbPath);
    }
    public String createUser(String email, String name){
        DatabaseReference userRef = dbRef.child("users");
        DatabaseReference pushedUserRef = userRef.push();
        pushedUserRef.setValue(new User(email, name));
        return pushedUserRef.getKey();
    }
    public Object createPin(LatLng coordinate){
        return null;
    }
    public Object createEvent(int pin_id, String description, String locationName){
        return null;
    }
    public Object createFood(int event_id, String description){
        return null;
    }
}
