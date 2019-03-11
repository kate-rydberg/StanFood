package stanford.cs194.stanfood.models;

public class Setting {
    private String userId;
    private boolean receivePushNotifications;
    private long timeWindowStart;
    private long timeWindowEnd;

    public Setting() {}

    public Setting(String userId, boolean receivePushNotifications, long timeWindowStart, long timeWindowEnd) {
        this.userId = userId;
        this.receivePushNotifications = receivePushNotifications;
        this.timeWindowStart = timeWindowStart;
        this.timeWindowEnd = timeWindowEnd;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean getReceivePushNotifications() {
        return receivePushNotifications;
    }

    public void setReceivePushNotifications(boolean receivePushNotifications) {
        this.receivePushNotifications = receivePushNotifications;
    }

    public long getTimeWindowStart() {
        return timeWindowStart;
    }

    public void setTimeWindowStart(long timeWindowStart) {
        this.timeWindowStart = timeWindowStart;
    }

    public long getTimeWindowEnd() {
        return timeWindowEnd;
    }

    public void setTimeWindowEnd(long timeWindowEnd) {
        this.timeWindowEnd = timeWindowEnd;
    }
}