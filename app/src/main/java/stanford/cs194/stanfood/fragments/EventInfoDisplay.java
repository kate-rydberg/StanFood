package stanford.cs194.stanfood.fragments;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.view.ViewGroup;

public class EventInfoDisplay {

    private Context context;
    private String name;
    private String location;
    private String time;
    private String description;
    private FragmentManager supportFragment;

    public EventInfoDisplay(
            Context context,
            String eventName,
            String eventLocation,
            String eventTime,
            String eventDescription,
            FragmentManager supportFragment
    ) {
        this.context = context;
        // initialize when creating object so that there can be getters and setters
        this.name = eventName;
        this.location = eventLocation;
        this.time = eventTime;
        this.description = eventDescription;
        this.supportFragment = supportFragment;

    }

    /**
     * Inflates a new view, sets the relevant fields in the view, and
     * replaces the old view with the new view.
     *
     * @param bottomSheetContents - the parent of the event info view
     */
    public void displayInfo(ViewGroup bottomSheetContents) {
        PopUpFragment eventPopUp = new PopUpFragment();
        eventPopUp.newInstance(name, location, time, description, bottomSheetContents).show(supportFragment,null);
    }
}
