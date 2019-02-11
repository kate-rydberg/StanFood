package stanford.cs194.stanfood;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Attr;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ListActivity extends ListView {
    private Database db;
    private ArrayList<String> name;
    private ArrayList<Long> time;
    private ArrayList<String> description;
    private Context context;
    private LatLng markerlocation;
    private HashMap<LatLng,String> eventStorage;

    public ListActivity(Context context, AttributeSet attr, Database db, Marker marker, HashMap<LatLng, String> eventStorage) {
        super(context,attr);
        this.context = context;
        this.db = db;
        this.markerlocation = marker.getPosition();
        this.eventStorage = eventStorage;

        createEventList();

        for(int i = 0; i < name.size(); i++){
            this.addView(getView(i));
        }

    }

    private void createEventList(){
        db.dbRef.child("event").addListenerForSingleValueEvent(
            new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot ds : dataSnapshot.getChildren()){
                        if(ds.hasChildren()){
                            Event event = ds.getValue(Event.class);
                            if(event.getPinId() == eventStorage.get(markerlocation)){
                                description.add(event.getDescription());
                                time.add(event.getTimeStart());
                                name.add(event.getEventId());
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

    private View getView(int position){
        LayoutInflater inflater= LayoutInflater.from(context);;
        View rowView=inflater.inflate(R.layout.list_item, null,true);

        //this code gets references to objects in the list_item.xml file
        TextView eventName = rowView.findViewById(R.id.eventName);
        TextView eventTimeStart = rowView.findViewById(R.id.eventTimeStart);
        TextView eventDescription = rowView.findViewById(R.id.eventDescription);

        //this code sets the values of the objects to values from the arraylists
        eventName.setText(name.get(position));
        eventTimeStart.setText(Long.toString(time.get(position)));
        eventDescription.setText(description.get(position));

        return rowView;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (canScrollVertically(this)) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
        return super.onTouchEvent(ev);
    }

    public boolean canScrollVertically (AbsListView view) {
        boolean canScroll = false;

        if (view != null && view.getChildCount() > 0) {
            boolean isOnTop = view.getFirstVisiblePosition() != 0 || view.getChildAt(0).getTop() != 0;
            boolean isAllItemsVisible = isOnTop && view.getLastVisiblePosition() == view.getChildCount();

            if (isOnTop || isAllItemsVisible) {
                canScroll = true;
            }
        }

        return canScroll;
    }
}
