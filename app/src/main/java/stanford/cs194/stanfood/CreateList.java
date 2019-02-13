package stanford.cs194.stanfood;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
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
    public ListActivity EVENTLIST;

    public CreateList(Context context, Database db, Marker marker, HashMap<LatLng, String> eventStorage) {
        this.context = context;
        this.db = db;
        this.markerlocation = marker.getPosition();
        this.eventStorage = eventStorage;

        createEventList();
        EVENTLIST = new ListActivity(context, name, time, description);
    }

    private void createEventList(){
        db.dbRef.child("event").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.d("shit","ondatachange");
                        for(DataSnapshot ds : dataSnapshot.getChildren()){
                            Log.d("shit","for loop");
                            if(ds.hasChildren()){
                                Event event = ds.getValue(Event.class);
                                Log.d("shit", eventStorage.get(markerlocation).toString());
                                Log.d("shit", event.getPinId().toString());
                                if(event.getPinId() == eventStorage.get(markerlocation)){
                                    Log.d("shit","hello?");
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

    public class ListActivity extends ArrayAdapter {

        private ArrayList<String> name;
        private ArrayList<Long> time;
        private ArrayList<String> description;

        private Context context;

        public ListActivity(Context context, ArrayList<String> name, ArrayList<Long> time, ArrayList<String> description) {
            super(context,R.layout.list_view,name);
            this.context = context;
            this.name = name;
            this.time = time;
            this.description = description;

            for(int i = 0; i < name.size(); i++){
                this.add(getView(i));
            }

        }

        private View getView(int position){
            LayoutInflater inflater= LayoutInflater.from(context);;
            View rowView=inflater.inflate(R.layout.list_view, null,true);

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
    }
}

