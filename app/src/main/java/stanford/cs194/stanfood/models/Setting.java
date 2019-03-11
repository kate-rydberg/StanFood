package stanford.cs194.stanfood.models;

public class Setting {
    private boolean receivePushNotifications;
    private int timeWindowStart;
    private int timeWindowEnd;

    public Setting() {}

    public Setting(boolean receivePushNotifications, int timeWindowStart, int timeWindowEnd) {
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

    public int getTimeWindowStart() {
        return timeWindowStart;
    }

    public void setTimeWindowStart(int timeWindowStart) {
        this.timeWindowStart = timeWindowStart;
    }

    public int getTimeWindowEnd() {
        return timeWindowEnd;
    }

    public void setTimeWindowEnd(int timeWindowEnd) {
        this.timeWindowEnd = timeWindowEnd;
    }
}