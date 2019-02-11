package stanford.cs194.stanfood;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

public class Notification {
    final private String CHANNEL_ID = "123";
    final private long DEFAULT_NOTIFICATION_TIMEOUT_MS = 3600000; // 1 hour
    final private Context context;
    private NotificationManagerCompat notificationManagerCompat;

    public Notification(Context context) {
        this.context = context;
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.channel_name);
            String description = context.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManagerCompat = NotificationManagerCompat.from(context);
    }

    /**
     * Creates and sends a new push notification to be displayed to the user
     *
     * @param title - title of the notification to be displayed to the user
     * @param content - text content of the notification to be displayed to the user
     * @param notificationId - unique id to interact with the notification in the future,
     *                       e.g. cancel the notification
     */
    public void sendNotification(String title, String content, int notificationId) {
        Log.d("Notification", "Sending notification with title: " + title
                + ", content: " + content + ", notificationId: " + notificationId);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(content)
                .setTimeoutAfter(DEFAULT_NOTIFICATION_TIMEOUT_MS)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // notificationId is a unique int for each notification that you must define
        notificationManagerCompat.notify(notificationId, mBuilder.build());
    }
}
