package stanford.cs194.stanfood;

import java.time.LocalDateTime;

public class Event {
    private String eventId;
    private String pinId;
    private String description;
    private String locationName;
    private LocalDateTime time;
    private Food food;

    public Event() {}

    public Event(String pinId, String description, String locationName, LocalDateTime time) {
        this.pinId = pinId;
        this.description = description;
        this.locationName = locationName;
        this.time = time;
    }

    public Event(String pinId, String description, String locationName, LocalDateTime time, Food food) {
        this.pinId = pinId;
        this.description = description;
        this.locationName = locationName;
        this.time = time;
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

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public Food getFood() { return food; }

    public void setFood(Food food) { this.food = food; }
}