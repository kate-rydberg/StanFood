package stanford.cs194.stanfood.database;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Adapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import stanford.cs194.stanfood.adapters.EventAdapter;
import stanford.cs194.stanfood.models.Event;

public class CreateList {

    private Context context;
    private Database db;
    private LatLng markerLocation;
    private HashMap<LatLng,String> eventStorage;

    private ArrayList<Event> events;
    private ListView eventListView;

    public CreateList(Context context, Database db, Marker marker,
                      HashMap<LatLng, String> eventStorage, ListView eventListView) {
        this.context = context;
        this.db = db;
        this.markerLocation = marker.getPosition();
        this.eventStorage = eventStorage;
        this.eventListView = eventListView;
        this.events = new ArrayList<>();
    }

    public void createEventList(){
        db.dbRef.child("events").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds : dataSnapshot.getChildren()){
                            if(ds.hasChildren()){
                                Event event = ds.getValue(Event.class);
                                if(event.getPinId().equals(eventStorage.get(markerLocation))){
                                    events.add(event);
                                }
                            }
                        }
                        Collections.sort(events);
                        Adapter rowCells = new EventAdapter(eventListView.getContext(), events, eventListView);
                        eventListView.setAdapter((ListAdapter) rowCells);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("ERROR",databaseError.toString());
                    }
                }
        );
    }

}

