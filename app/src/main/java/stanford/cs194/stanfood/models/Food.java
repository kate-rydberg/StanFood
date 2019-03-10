package stanford.cs194.stanfood.models;

public class Food {

    private String foodId;
    private String eventId;
    private String description;
    private String imagePath;

    public Food() {}

    public Food(String eventId, String description, String imagePath) {
        this.eventId = eventId;
        this.description = description;
        this.imagePath = imagePath;
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

    public String getImagePath() { return imagePath; }

    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
}
