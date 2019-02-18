package stanford.cs194.stanfood.models;

import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.Date;

public class Event implements Comparable<Event> {
    private String eventId;
    private String pinId;
    private String name;
    private String description;
    private String locationName;
    private long timeStart;
    private long duration;
    private String userID;
    private Food food;

    public Event() {}

    public Event(String pinId, String name, String description, String locationName, long timeStart,
                 long duration, String userID) {
        this.pinId = pinId;
        this.name = name;
        this.description = description;
        this.locationName = locationName;
        this.timeStart = timeStart;
        this.duration = duration;
        this.userID = userID;
    }

    public Event(String pinId, String name, String description, String locationName, long timeStart,
                 long duration, String userID, Food food) {
        this.pinId = pinId;
        this.name = name;
        this.description = description;
        this.locationName = locationName;
        this.timeStart = timeStart;
        this.duration = duration;
        this.userID = userID;
        this.food = food;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getPinId() { return pinId; }

    public void setPinId(String pinId) { this.pinId = pinId; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public long getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(long timeStart) {
        this.timeStart = timeStart;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public boolean eventExpired(){
        Date timeExpire = new Date(timeStart + duration);
        return timeExpire.before(new Date());
    }

    public Food getFood() { return food; }

    public void setFood(Food food) { this.food = food; }

    public String getUserID() {
        return userID;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int compareTo(Event o) {
        return Long.compare(getTimeStart(), o.getTimeStart());
    }
}