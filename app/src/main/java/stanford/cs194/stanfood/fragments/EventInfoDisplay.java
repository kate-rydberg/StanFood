package stanford.cs194.stanfood.fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import stanford.cs194.stanfood.R;
import stanford.cs194.stanfood.helpers.ViewGroupUtils;

public class EventInfoDisplay {

    private Context context;
    private String name;
    private String location;
    private String time;
    private String description;

    public EventInfoDisplay(
            Context context,
            String eventName,
            String eventLocation,
            String eventTime,
            String eventDescription
    ) {
        this.context = context;
        // initialize when creating object so that there can be getters and setters
        this.name = eventName;
        this.location = eventLocation;
        this.time = eventTime;
        this.description = eventDescription;
    }

    /**
     * Inflates a new view, sets the relevant fields in the view, and
     * replaces the old view with the new view.
     *
     * @param bottomSheetContents - the parent of the event info view
     */
    public void displayInfo(ViewGroup bottomSheetContents) {
        LayoutInflater viewInflater = LayoutInflater.from(context);
        View infoView = viewInflater.inflate(R.layout.event_info, null, true);

        TextView infoHeader = bottomSheetContents.findViewById(R.id.bottom_sheet_header);
        TextView infoLocationName = infoView.findViewById(R.id.infoLocationName);
        TextView infoEventTime = infoView.findViewById(R.id.infoEventTime);
        TextView infoEventDescription = infoView.findViewById(R.id.infoEventDescription);

        infoHeader.setText(name);
        String locationText = infoLocationName.getText().toString() + location;
        infoLocationName.setText(locationText);
        String timeText = infoEventTime.getText().toString() + time;
        infoEventTime.setText(timeText);
        infoEventDescription.setText(description);

        ViewGroupUtils.replaceViewById(infoView, bottomSheetContents, 1);
    }
}
