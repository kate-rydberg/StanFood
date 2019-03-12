package stanford.cs194.stanfood.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.appyvet.materialrangebar.RangeBar;
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
        Switch receivePush = findViewById(R.id.receivePush);
        receivePush.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                RangeBar timeWindow = findViewById(R.id.timeWindow);
                timeWindow.setEnabled(isChecked);
            }
        });
        readSettings();
    }

    /*
     * Reads the settings when the activity is loaded to show the user
     * their previously selected settings.
     */
    private void readSettings() {
        final String userId = prefs.getString("userId", "");
        db.dbRef.child("settings").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Setting setting = dataSnapshot.getValue(Setting.class);
                Switch receivePush = findViewById(R.id.receivePush);
                RangeBar timeWindow = findViewById(R.id.timeWindow);
                boolean doReceivePush = setting.getReceivePushNotifications();
                int timeWindowStart = setting.getTimeWindowStart();
                int timeWindowEnd = setting.getTimeWindowEnd();
                timeWindow.setEnabled(doReceivePush);
                timeWindow.setRangePinsByIndices(timeWindowStart, timeWindowEnd);
                receivePush.setChecked(doReceivePush);
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
        RangeBar timeWindow = findViewById(R.id.timeWindow);
        boolean doReceivePush = receivePush.isChecked();
        String userId = prefs.getString("userId", "");
        int timeWindowStart = timeWindow.getLeftIndex();
        int timeWindowEnd = timeWindow.getRightIndex();
        db.createSetting(userId, doReceivePush, timeWindowStart, timeWindowEnd);
        finish();
    }
}