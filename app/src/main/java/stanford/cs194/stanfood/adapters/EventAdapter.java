package stanford.cs194.stanfood.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import stanford.cs194.stanfood.App;
import stanford.cs194.stanfood.R;
import stanford.cs194.stanfood.activities.PopupActivity;
import stanford.cs194.stanfood.fragments.BottomSheetListView;
import stanford.cs194.stanfood.models.Event;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;


public class EventAdapter extends ArrayAdapter {

    private ArrayList<Event> events;
    private Context context;

    private BottomSheetListView eventListView;
    private ViewGroup bottomSheetContentsView;

    public EventAdapter(
            Context context,
            ArrayList<Event> events,
            BottomSheetListView eventListView,
            ViewGroup bottomSheetContentsView
    ) {
        super(context, R.layout.list_view, events);
        this.context = context;
        this.events = events;
        this.eventListView = eventListView;
        this.bottomSheetContentsView = bottomSheetContentsView;
    }

    @NonNull
    public View getView(int position, final View view, @NonNull ViewGroup parent) {
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
        // TODO: Temporary null check until we clear out data since some events don't have explicit name fields
        if(name != null && !name.equals("")) eventName.setText(name);
        else  eventName.setText("N/A");

        if(locationName != null) eventLocation.setText(locationName);
        else eventLocation.setText("N/A");

        if(time != 0) eventTimeStart.setText(getEventTimeRange(time, duration));
        else eventTimeStart.setText("N/A");

        if(!description.equals("")) eventDescription.setText(description);
        else eventDescription.setText("N/A");

        rowView.setOnClickListener(new View.OnClickListener(){
            /**
             * When list item is clicked on, display the event information.
             * @param listItemView
             */
            @Override
            public void onClick(View listItemView) {
                LayoutInflater inflater = (LayoutInflater) App.getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.event_popup, null);
                String clickedEventName = ((TextView)listItemView.findViewById(R.id.eventName)).getText().toString();
                String clickedTimeRange = ((TextView)listItemView.findViewById(R.id.eventTimeStart)).getText().toString();
                String clickedEventDescription = ((TextView)listItemView.findViewById(R.id.eventDescription)).getText().toString();

                TextView infoHeader = bottomSheetContentsView.findViewById(R.id.bottom_sheet_header);
                String clickedLocationName = infoHeader.getText().toString();
                TextView infoLocationName = popupView.findViewById(R.id.infoLocationName);
                TextView infoEventTime = popupView.findViewById(R.id.infoEventTime);
                TextView infoEventDescription = popupView.findViewById(R.id.infoEventDescription);

                String locationText = infoLocationName.getText().toString() + clickedLocationName;
                infoLocationName.setText(locationText);
                String timeText = infoEventTime.getText().toString() + clickedTimeRange;
                infoEventTime.setText(timeText);
                infoEventDescription.setText(clickedEventDescription);

                Intent popUpActivitiy = new Intent(App.getContext(),PopupActivity.class);
                App.getContext().startActivity(popUpActivitiy);
                //create the popup window
                int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                boolean focusable = true; // lets taps outside the popup also dismiss it
                final PopupWindow eventPopUp = new PopupWindow(popupView, width, height, focusable);

                // which view you pass in doesn't matter, it is only used for the window tolken
                eventPopUp.showAtLocation(rowView, Gravity.CENTER, 0, -200);
                popupView.setFocusable(true);
                // dismiss the popup window when touched
                popupView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        eventPopUp.dismiss();
                        return true;
                    }
                });

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    eventPopUp.setElevation(20);
                }
            }
        });

        return rowView;
    }

    /**
     * Given the start time and duration, returns the time range.
     *
     * E.g. given [Mon Jan 15, 4:30PM] and duration [90 min] in milliseconds,
     * return the string "Mon Jan 15, 4:30PM - 6:00PM"
     */
    private String getEventTimeRange(long startTimeInMillis, long durationInMillis) {
        // set start time
        Calendar startTime = Calendar.getInstance();
        startTime.setTimeInMillis(startTimeInMillis);
        // set end time
        Calendar endTime = Calendar.getInstance();
        endTime.setTimeInMillis(startTimeInMillis + durationInMillis);

        SimpleDateFormat startFormat = new SimpleDateFormat(/*"E MMM dd,*/"hh:mm", Locale.US);
        SimpleDateFormat endFormat = new SimpleDateFormat("hh:mma", Locale.US);
        String startTimeStr = startFormat.format(startTime.getTime());
        String endTimeStr = endFormat.format(endTime.getTime());
        return startTimeStr + " - " + endTimeStr;
    }

}
