package stanford.cs194.stanfood;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class Pin {
    private String pinId;
    private LatLng locationCoordinate;
    private String locationName;
    private int numEvents;

    public Pin() {}

    public Pin(LatLng locationCoordinate) {
        this.locationCoordinate = locationCoordinate;
    }

    public String getPinId() {
        return pinId;
    }

    public void setPinId(String pinId) {
        this.pinId = pinId;
    }

    public LatLng getLocationCoordinate() {
        return locationCoordinate;
    }

    public void setLocationCoordinate(LatLng locationCoordinate) {
        this.locationCoordinate = locationCoordinate;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public int getNumEvents() {
        return numEvents;
    }

    /*
     * Returns list of events corresponding to pin's location.
     * TODO: Need to implement
     */
    public ArrayList<Event> getEvents(String pinId) {
        ArrayList<Event> events = new ArrayList<>();
        // Lookup from database to get all events with same location using pinId
        numEvents = events.size();
        return events;
    }
}