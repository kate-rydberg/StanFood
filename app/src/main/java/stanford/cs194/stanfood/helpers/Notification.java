package stanford.cs194.stanfood.helpers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import stanford.cs194.stanfood.R;
import stanford.cs194.stanfood.activities.MapsActivity;
import stanford.cs194.stanfood.database.Database;
import stanford.cs194.stanfood.models.Event;

public class Notification {
    final private String CHANNEL_ID = "123";
    final private long DEFAULT_NOTIFICATION_TIMEOUT_MS = 3600000; // 1 hour
    final private Context context;
    final private Database db;
    private NotificationManagerCompat notificationManagerCompat;

    public Notification(Context context, Database db) {
        this.context = context;
        this.db = db;
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
    public void sendNotification(final String title, final String content, final int notificationId) {
        Log.d("Notification", "Sending notification with title: " + title
                + ", content: " + content + ", notificationId: " + notificationId);

        db.dbRef.child("events").child(content).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot){
                        Event event = dataSnapshot.getValue(Event.class);
                        String name = event.getName();
                        String time = TimeDateUtils.getEventTimeRange(event.getTimeStart(), event.getDuration());
                        String location = event.getLocationName();
                        String description = event.getDescription();
                        String body = String.format("%s at %s, %s", name, time, location);
                        sendNotificationWithEventDetails(title, body, content, notificationId,
                                name, location, time, description);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("ERROR",databaseError.toString());
                    }
                }
        );


    }

    private void sendNotificationWithEventDetails(final String title, final String content,
                                                  final String eventId, final int notificationId,
                                                  final String name, final String location,
                                                  final String time, final String description) {
        Intent intent = new Intent(context, MapsActivity.class);
        Bundle extras = new Bundle();
        extras.putString("openPopup", eventId);
        extras.putString("clickedEventName", name);
        extras.putString("clickedLocationName", location);
        extras.putString("clickedTimeRange", time);
        extras.putString("clickedEventDescription", description);
        intent.putExtras(extras);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(content)
                .setTimeoutAfter(DEFAULT_NOTIFICATION_TIMEOUT_MS)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // notificationId is a unique int for each notification that you must define
        notificationManagerCompat.notify(notificationId, mBuilder.build());
    }
}
