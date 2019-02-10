package stanford.cs194.stanfood;

import com.google.android.gms.maps.model.LatLng;

public class Pin {
    private String pinId;
    private LatLngWrapper locationCoordinate;
    private String locationName;
    private int numEvents;

    public Pin() {}

    public Pin(LatLngWrapper locationCoordinate) {
        this.locationCoordinate = locationCoordinate;
        this.numEvents = 1;
    }

    public Pin(LatLngWrapper locationCoordinate, String locationName, int numEvents) {
        this.locationCoordinate = locationCoordinate;
        this.locationName = locationName;
        this.numEvents = numEvents;
    }

    public String getPinId() {
        return pinId;
    }

    public void setPinId(String pinId) {
        this.pinId = pinId;
    }

    public LatLng getLocationCoordinate() {
        return locationCoordinate.getLatLng();
    }

    public void setLocationCoordinate(LatLngWrapper locationCoordinate) {
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
}