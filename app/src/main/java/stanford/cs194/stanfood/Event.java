package stanford.cs194.stanfood;

import java.time.LocalDateTime;

public class Event {
    private String eventId;
    private String description;
    private String locationName;
    private LocalDateTime time;

    public Event() {}

    public Event(String eventId) {
        this.eventId = eventId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
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

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    /*
     * Gets the food of the current event
     */
    public Food getFood(String eventId) {
        Food food = new Food();
        // Lookup food from database using eventId
        return food;
    }
}