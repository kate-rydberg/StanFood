package stanford.cs194.stanfood.models;

public class Setting {
    private boolean receivePushNotifications;
    private String timeWindowStart;
    private String timeWindowEnd;

    public Setting() {}

    public Setting(boolean receivePushNotifications, String timeWindowStart, String timeWindowEnd) {
        this.receivePushNotifications = receivePushNotifications;
        this.timeWindowStart = timeWindowStart;
        this.timeWindowEnd = timeWindowEnd;
    }

    public boolean getReceivePushNotifications() {
        return receivePushNotifications;
    }

    public void setReceivePushNotifications(boolean receivePushNotifications) {
        this.receivePushNotifications = receivePushNotifications;
    }

    public String getTimeWindowStart() {
        return timeWindowStart;
    }

    public void setTimeWindowStart(String timeWindowStart) {
        this.timeWindowStart = timeWindowStart;
    }

    public String getTimeWindowEnd() {
        return timeWindowEnd;
    }

    public void setTimeWindowEnd(String timeWindowEnd) {
        this.timeWindowEnd = timeWindowEnd;
    }
}