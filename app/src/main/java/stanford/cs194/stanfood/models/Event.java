package stanford.cs194.stanfood.models;

import java.util.Date;

public class Event implements Comparable<Event> {
    private String eventId;
    private String pinId;
    private String description;
    private String locationName;
    private long timeStart;
    private long duration;
    private Food food;

    public Event() {}

    public Event(String pinId, String description, String locationName, long timeStart,
                 long duration) {
        this.pinId = pinId;
        this.description = description;
        this.locationName = locationName;
        this.timeStart = timeStart;
        this.duration = duration;
    }

    public Event(String pinId, String description, String locationName, long timeStart,
                 long duration, Food food) {
        this.pinId = pinId;
        this.description = description;
        this.locationName = locationName;
        this.timeStart = timeStart;
        this.duration = duration;
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

    @Override
    public int compareTo(Event o) {
        return Long.valueOf(getTimeStart()).compareTo(Long.valueOf(o.getTimeStart()));
    }
}