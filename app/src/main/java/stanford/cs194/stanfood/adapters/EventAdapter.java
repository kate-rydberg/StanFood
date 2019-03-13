package stanford.cs194.stanfood.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import stanford.cs194.stanfood.R;
import stanford.cs194.stanfood.fragments.BottomSheetListView;
import stanford.cs194.stanfood.fragments.PopUpFragment;
import stanford.cs194.stanfood.helpers.TimeDateUtils;
import stanford.cs194.stanfood.models.Event;

public class EventAdapter extends ArrayAdapter {

    private ArrayList<Event> events;
    private Context context;

    private FragmentManager supportFragment;
    private BottomSheetListView eventListView;
    private ViewGroup bottomSheetContentsView;

    public EventAdapter(
            Context context,
            ArrayList<Event> events,
            BottomSheetListView eventListView,
            ViewGroup bottomSheetContentsView,
            FragmentManager supportFragment
    ) {
        super(context, R.layout.list_view, events);
        this.context = context;
        this.events = events;
        this.eventListView = eventListView;
        this.bottomSheetContentsView = bottomSheetContentsView;
        this.supportFragment = supportFragment;
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

        Event event = events.get(position);
        String name = event.getName();
        final String locationName = event.getLocationName();
        long time = event.getTimeStart();
        long duration = event.getDuration();
        String description = event.getDescription();

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

                PopUpFragment.newInstance(clickedEventName, clickedLocationName, clickedTimeRange, clickedEventDescription, bottomSheetContentsView).show(supportFragment,null);
            }
        });

        return rowView;
    }

}
