package stanford.cs194.stanfood;

public class Food {

    private String foodId;
    private String eventId;
    private String description;

    public Food() {}

    public Food(String eventId, String description) {
        this.eventId = eventId;
        this.description = description;
    }

    public String getFoodId() {
        return foodId;
    }

    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
}
