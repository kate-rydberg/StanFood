package stanford.cs194.stanfood.helpers;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import stanford.cs194.stanfood.App;
import stanford.cs194.stanfood.R;
import stanford.cs194.stanfood.authentication.Authentication;
import stanford.cs194.stanfood.database.Database;

public class CloudMessaging extends FirebaseMessagingService {
    private static final String TAG = "CloudMessaging";
    private Database db = new Database();
    private Authentication auth = new Authentication();
    private Notification notif = new Notification(App.getContext());

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // There are two types of messages data messages and notification messages. Data messages
        // are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data
        // messages are the type
        // traditionally used with GCM. Notification messages are only received here in
        // onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated
        // notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages
        // containing both notification
        // and data payloads are treated as notification messages. The Firebase console always
        // sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            String title = getResources().getString(R.string.new_event_notification_title);
            String body = remoteMessage.getNotification().getBody();
            Log.d(TAG, "Message Notification Body: " + body);
            int notificationId = body.hashCode(); // TODO Uniqueness not guaranteed

            notif.sendNotification(title, body, notificationId);
        }
    }

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);
        if (auth.getCurrentUser() != null) {
            String userId = auth.getCurrentUser().getUid();
            db.updateUserInstanceId(token, userId);
        }
    }
}
