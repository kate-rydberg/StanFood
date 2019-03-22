package stanford.cs194.stanfood.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import stanford.cs194.stanfood.R;
import stanford.cs194.stanfood.database.Database;
import stanford.cs194.stanfood.helpers.TimeDateUtils;
import stanford.cs194.stanfood.models.Event;

public class DeleteEventAdapter extends ArrayAdapter {
    private ArrayList<Event> events;
    private Context context;
    private Database db;

    public DeleteEventAdapter(Context context, ArrayList<Event> events, Database db) {
        super(context, R.layout.list_view, events);
        this.context = context;
        this.events = events;
        this.db = db;
    }

    @NonNull
    public View getView(int position, final View view, @NonNull final ViewGroup parent) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View rowView = inflater.inflate(R.layout.list_view, null, true);

        // gets references to objects in the list_view.xml file
        TextView eventName = rowView.findViewById(R.id.eventName);
        TextView eventTimeStart = rowView.findViewById(R.id.eventTimeStart);
        TextView eventDescription = rowView.findViewById(R.id.eventDescription);

        final Event event = events.get(position);
        String name = event.getName();
        long time = event.getTimeStart();
        long duration = event.getDuration();
        String description = event.getDescription();

        // sets the values of the objects to the value from the current event
        // TODO: Remove null check when we clear out data since some events don't have explicit name fields
        if(name != null && !name.equals("")) eventName.setText(name);
        else eventName.setText("N/A");

        if(time != 0) eventTimeStart.setText(TimeDateUtils.getEventTimeRange(time, duration));
        else eventTimeStart.setText("N/A");

        if(!description.equals("")) eventDescription.setText(description);
        else eventDescription.setText("N/A");

        rowView.setOnClickListener(new View.OnClickListener(){
            /**
             * When list item is clicked on, delete event
             * @param listItemView The list view to contain all of the event items
             */
            @Override
            public void onClick(View listItemView) {
                db.deleteEvent(event);
                String text = "Event Deleted!\nRefresh View for Updated List.";
                Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
                toast.show();
            }
        });
        return rowView;
    }
}