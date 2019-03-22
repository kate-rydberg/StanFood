package stanford.cs194.stanfood.database;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
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
import java.util.Date;
import java.util.HashMap;

import stanford.cs194.stanfood.adapters.DeleteEventAdapter;
import stanford.cs194.stanfood.adapters.EventAdapter;
import stanford.cs194.stanfood.fragments.BottomSheetListView;
import stanford.cs194.stanfood.models.Event;

public class CreateList {
    private Database db;
    private ArrayList<Event> events;
    private BottomSheetListView eventListView;
    private ViewGroup bottomSheetContentsView;
    private FragmentManager supportFragment;

    private Date startDate;
    private Date endDate;

    // without date filtering
    public CreateList(Database db, BottomSheetListView eventListView,
                      ViewGroup bottomSheetContentsView, FragmentManager supportFragment) {
        this.db = db;
        this.eventListView = eventListView;
        this.bottomSheetContentsView = bottomSheetContentsView;
        this.events = new ArrayList<>();
        this.supportFragment = supportFragment;
    }

    // overloaded constructor for date filtering
    public CreateList(Database db, BottomSheetListView eventListView,
                      ViewGroup bottomSheetContentsView, FragmentManager supportFragment,
                      Date startDate, Date endDate) {
        this.db = db;
        this.eventListView = eventListView;
        this.bottomSheetContentsView = bottomSheetContentsView;
        this.events = new ArrayList<>();
        this.supportFragment = supportFragment;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * Creates a list of all events with Pin Ids corresponding to the current marker location.
     * Creates an EventAdapter with this list to make a list view with all events
     */
    public void createLocationEventList(final String pinId){
        db.dbRef.child("events").orderByChild("pinId").equalTo(pinId).
                addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds : dataSnapshot.getChildren()){
                            Event event = ds.getValue(Event.class);
                            Date d = new Date(event.getTimeStart());
                            // if doing date filtering, make sure event date lies within range
                            if(startDate != null && endDate != null &&
                                    (d.before(startDate) || d.after(endDate)))
                                continue;
                            event.setEventId(ds.getKey());
                            events.add(event);
                        }
                        Collections.sort(events);
                        Adapter rowCells = new EventAdapter(
                                eventListView.getContext(),
                                events,
                                eventListView,
                                bottomSheetContentsView,
                                supportFragment
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

    /**
     * Creates a list of all events with User Ids corresponding to the current logged-in user.
     * Creates a DeleteEventAdapter with this list to make a list view with all events
     */
    public void createUserEventList(final String userId){
        db.dbRef.child("events").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds : dataSnapshot.getChildren()){
                            if(ds.hasChildren()){
                                Event event = ds.getValue(Event.class);
                                if (event != null && event.getUserId() != null && event.getUserId().equals(userId)) {
                                    event.setEventId(ds.getKey());
                                    events.add(event);
                                }
                            }
                        }
                        Collections.sort(events);
                        ListAdapter rowCells = new DeleteEventAdapter(
                                eventListView.getContext(),
                                events,
                                db
                        );
                        eventListView.setAdapter(rowCells);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("ERROR", databaseError.toString());
                    }
                }
        );
    }
}

