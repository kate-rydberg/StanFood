package stanford.cs194.stanfood;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Database {
    final private String dbPath = "https://stanfood-e7255.firebaseio.com/";
    private FirebaseDatabase database;
    private DatabaseReference dbRef;

    public Database(){
        database = FirebaseDatabase.getInstance(dbPath);
        dbRef = database.getReference();
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
    public Object createEvent(String description, String locationName){
        return null;
    }
    public Object createFood(int event_id, String description){
        return null;
    }
}
