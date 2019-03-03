package stanford.cs194.stanfood.database;

import android.support.annotation.NonNull;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ListAdapter;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import stanford.cs194.stanfood.adapters.EventAdapter;
import stanford.cs194.stanfood.fragments.BottomSheetListView;
import stanford.cs194.stanfood.models.Event;

public class CreateList {

    private Database db;
    private LatLng markerLocation;
    private HashMap<LatLng,String> eventStorage;

    private ArrayList<Event> events;
    private BottomSheetListView eventListView;
    private ViewGroup bottomSheetContentsView;


    public CreateList(Database db, Marker marker,
                      HashMap<LatLng, String> eventStorage, BottomSheetListView eventListView,
                      ViewGroup bottomSheetContentsView) {
        this.db = db;
        this.markerLocation = marker.getPosition();
        this.eventStorage = eventStorage;
        this.eventListView = eventListView;
        this.bottomSheetContentsView = bottomSheetContentsView;
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
                                    event.setEventId(ds.getKey());
                                    events.add(event);
                                }
                            }
                        }
                        Collections.sort(events);
                        Adapter rowCells = new EventAdapter(
                                eventListView.getContext(),
                                events,
                                eventListView,
                                bottomSheetContentsView
                        );
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

