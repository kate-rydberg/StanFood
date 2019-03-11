package stanford.cs194.stanfood.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Switch;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import stanford.cs194.stanfood.R;
import stanford.cs194.stanfood.database.Database;
import stanford.cs194.stanfood.models.Setting;

public class UserSettingsActivity extends AppCompatActivity {
    private Database db;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);
        db = new Database();
        prefs = getSharedPreferences("loginData", MODE_PRIVATE);
        readSettings();
    }

    private void readSettings() {
        final Switch receivePush = findViewById(R.id.receivePush);
        final String userId = prefs.getString("userId", "");
        db.dbRef.child("settings").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Setting setting = dataSnapshot.getValue(Setting.class);
                if (setting != null) {
                    boolean doReceivePush = setting.getReceivePushNotifications();
                    int timeWindowStart = setting.getTimeWindowStart();
                    int timeWindowEnd = setting.getTimeWindowEnd();
                    receivePush.setChecked(doReceivePush);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("settings", "Reading settings for " + userId + " failed.");
            }
        });
    }

    /*
     * Saves the user preferences to the settings/ Firebase database
     */
    public void saveSettings(View view) {
        Switch receivePush = findViewById(R.id.receivePush);
        boolean doReceivePush = receivePush.isChecked();
        final String userId = prefs.getString("userId", "");
        db.createSetting(userId, doReceivePush, 0, 24);
        finish();
    }
}
