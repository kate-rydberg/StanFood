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
     * Creates and sends a new push notification of a new event to be displayed to the user
     *
     * @param eventId - eventId of the event to be displayed to the user
     */
    public void sendNotificationForEvent(final String eventId) {
        db.dbRef.child("events").child(eventId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot){
                        Event event = dataSnapshot.getValue(Event.class);
                        sendNotificationWithEventDetails(eventId, event);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("ERROR",databaseError.toString());
                    }
                }
        );
    }

    private void sendNotificationWithEventDetails(final String eventId, final Event event) {
        // Get general details to attach to the push notification
        String title = context.getResources().getString(R.string.new_event_notification_title);
        int notificationId = eventId.hashCode(); // TODO Uniqueness not guaranteed

        // Get event details to attach to the push notification
        String name = event.getName();
        String time = TimeDateUtils.getEventTimeRange(event.getTimeStart(), event.getDuration());
        String location = event.getLocationName();
        String description = event.getDescription();
        String body = String.format("%s at %s, %s", name, time, location);

        // Create a new intent that will open the main MapsActivity with the event popup fragment
        Intent intent = new Intent(context, MapsActivity.class);

        // Pass in event details to the event popup fragment
        Bundle extras = new Bundle();
        extras.putString("clickedEventId", eventId);
        extras.putString("clickedEventName", name);
        extras.putString("clickedLocationName", location);
        extras.putString("clickedTimeRange", time);
        extras.putString("clickedEventDescription", description);
        intent.putExtras(extras);

        // Set flags to make sure we open the correct intent (with the correct event) when a push
        // notification is tapped
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        sendNotification(title, body, notificationId, pendingIntent);
    }

    /**
     * Creates and sends a new push notification to be displayed to the user
     *
     * @param title - title of the notification to be displayed to the user
     * @param content - content to be displayed to the user
     * @param notificationId - unique id to interact with the notification in the future,
     *                       e.g. cancel the notification
     */
    public void sendNotification(final String title, final String content, final int notificationId,
                                 PendingIntent pendingIntent) {
        Log.d("Notification", "Sending notification with title: " + title
                + ", content: " + content + ", notificationId: " + notificationId);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(content)
                .setTimeoutAfter(DEFAULT_NOTIFICATION_TIMEOUT_MS)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        notificationManagerCompat.notify(notificationId, mBuilder.build());
    }
}
