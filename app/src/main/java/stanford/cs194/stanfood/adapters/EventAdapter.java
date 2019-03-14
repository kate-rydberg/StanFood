package stanford.cs194.stanfood.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import stanford.cs194.stanfood.R;
import stanford.cs194.stanfood.database.Database;
import stanford.cs194.stanfood.fragments.BottomSheetListView;
import stanford.cs194.stanfood.fragments.PopUpFragment;
import stanford.cs194.stanfood.helpers.TimeDateUtils;
import stanford.cs194.stanfood.models.Event;
import stanford.cs194.stanfood.models.Food;

public class EventAdapter extends ArrayAdapter {

    private ArrayList<Event> events;
    private Context context;

    private FragmentManager supportFragment;
    private BottomSheetListView eventListView;
    private ViewGroup bottomSheetContentsView;
    private Database db;
    private Display screen;

    private String url;

    public EventAdapter(
            Context context,
            ArrayList<Event> events,
            BottomSheetListView eventListView,
            ViewGroup bottomSheetContentsView,
            FragmentManager supportFragment,
            Database db,
            Display screen
    ) {
        super(context, R.layout.list_view, events);
        this.context = context;
        this.events = events;
        this.eventListView = eventListView;
        this.bottomSheetContentsView = bottomSheetContentsView;
        this.supportFragment = supportFragment;
        this.db = db;
        this.screen = screen;
        this.url = null;
    }

    @NonNull
    public View getView(int position, final View view, @NonNull final ViewGroup parent) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View rowView = inflater.inflate(R.layout.list_view, null, true);

        // gets references to objects in the list_view.xml file
        TextView eventLocation = bottomSheetContentsView.findViewById(R.id.bottom_sheet_header);
        TextView eventName = rowView.findViewById(R.id.eventName);
        TextView eventTimeStart = rowView.findViewById(R.id.eventTimeStart);
        TextView eventDescription = rowView.findViewById(R.id.eventDescription);

        final Event event = events.get(position);
        String name = event.getName();
        final String locationName = event.getLocationName();
        long time = event.getTimeStart();
        long duration = event.getDuration();
        String description = event.getDescription();
        loadFoodImages(event.getEventId(),rowView);

        // sets the values of the objects to the value from the current event
        // TODO: Remove null check when we clear out data since some events don't have explicit name fields
        if(name != null && !name.equals("")) eventName.setText(name);
        else  eventName.setText("N/A");

        if(locationName != null) eventLocation.setText(locationName);
        else eventLocation.setText("N/A");

        if(time != 0) eventTimeStart.setText(TimeDateUtils.getEventTimeRange(time, duration));
        else eventTimeStart.setText("N/A");

        if(!description.equals("")) eventDescription.setText(description);
        else eventDescription.setText("N/A");

        rowView.setOnClickListener(new View.OnClickListener(){
            /**
             * When list item is clicked on, display the event information.
             * @param listItemView The list view to contain all of the event items
             */
            @Override
            public void onClick(View listItemView) {
                String clickedEventName = ((TextView)listItemView.findViewById(R.id.eventName)).getText().toString();
                String clickedTimeRange = ((TextView)listItemView.findViewById(R.id.eventTimeStart)).getText().toString();
                String clickedEventDescription = ((TextView)listItemView.findViewById(R.id.eventDescription)).getText().toString();

                TextView bottomSheetHeader = bottomSheetContentsView.findViewById(R.id.bottom_sheet_header);
                String clickedLocationName = bottomSheetHeader.getText().toString();

                PopUpFragment eventPopUp = new PopUpFragment();
                eventPopUp.newInstance(clickedEventName, clickedLocationName, clickedTimeRange, clickedEventDescription, event.getEventId(), screen, db).show(supportFragment,null);
            }
        });

        return rowView;
    }

    /**
     * @param eventId
     * TODO: Retrieves images from Storage and loads into Picasso adapter
     */
    private void loadFoodImages(final String eventId, final View rowView){
        final ImageView eventImage = rowView.findViewById(R.id.eventImage);
        db.dbRef.child("food").orderByChild("eventId").equalTo(eventId).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            Food food = ds.getValue(Food.class);
                            url = food.getImagePath();

                            Picasso.get()
                                    .load(url)
                                    .fit()
                                    .error(R.drawable.no_picture)
                                    .into(eventImage);

                        }
                        if (url == null) {
                            Drawable drawable = context.getResources().getDrawable(R.drawable.no_picture);
                            eventImage.setImageDrawable(drawable);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("ERROR", databaseError.toString());
                    }
                });
    }
}
