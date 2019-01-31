package stanford.cs194.stanfood;

import java.time.LocalDateTime;

public class Event {
    private String eventId;
    private String description;
    private String locationName;
    private LocalDateTime time;

    public Event() {}

    public Event(String description, String locationName, LocalDateTime time) {
        this.description = description;
        this.locationName = locationName;
        this.time = time;
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
     * TODO: Need to implement
     */
    public Food getFood(String eventId) {
        Food food = new Food("testId", "testDescription");
        // Lookup food from database using eventId
        return food;
    }
}