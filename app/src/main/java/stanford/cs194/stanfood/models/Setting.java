package stanford.cs194.stanfood.models;

public class Setting {
    private String userId;
    private boolean receivePush;
    private long timeWindowStart;
    private long timeWindowEnd;

    public Setting() {}

    public Setting(String userId, boolean receivePush, long timeWindowStart, long timeWindowEnd) {
        this.userId = userId;
        this.receivePush = receivePush;
        this.timeWindowStart = timeWindowStart;
        this.timeWindowEnd = timeWindowEnd;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean getReceivePush() {
        return receivePush;
    }

    public void setReceivePush(boolean receivePush) {
        this.receivePush = receivePush;
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