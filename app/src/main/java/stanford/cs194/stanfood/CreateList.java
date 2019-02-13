package stanford.cs194.stanfood;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class CreateList {

    private Context context;
    private Database db;
    private LatLng markerlocation;
    private HashMap<LatLng,String> eventStorage;

    private ArrayList<String> name;
    private ArrayList<Long> time;
    private ArrayList<String> description;
    private ListView eventList;

    public CreateList(Context context, Database db, Marker marker, HashMap<LatLng, String> eventStorage, ListView eventList) {
        this.context = context;
        this.db = db;
        this.markerlocation = marker.getPosition();
        this.eventStorage = eventStorage;
        this.eventList = eventList;
        name = new ArrayList<>();
        time = new ArrayList<>();
        description = new ArrayList<>();

        createEventList();
    }

    private void createEventList(){
        db.dbRef.child("events").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds : dataSnapshot.getChildren()){
                            if(ds.hasChildren()){
                                Event event = ds.getValue(Event.class);
                                if(event.getPinId().equals(eventStorage.get(markerlocation))){
                                    description.add(event.getDescription());
                                    time.add(event.getTimeStart());
                                    name.add(event.getLocationName());
                                }
                            }
                        }
                        Adapter rowCells = new EventAdapter(eventList.getContext(), name, time, description);
                        eventList.setAdapter((ListAdapter) rowCells);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("ERROR",databaseError.toString());
                    }
                }
        );
    }

    public ArrayList<String> getEventNames(){return name;}

    public ArrayList<String> getEventDescriptions(){return description;}

    public ArrayList<Long> getEventStartTime(){return time;}

}

