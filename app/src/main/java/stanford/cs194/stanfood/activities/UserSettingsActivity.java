package stanford.cs194.stanfood.activities;

import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TimePicker;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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
                TextInputLayout selectStartTime = findViewById(R.id.selectStartTime);
                TextInputLayout selectEndTime = findViewById(R.id.selectEndTime);
                selectStartTime.setEnabled(isChecked);
                selectEndTime.setEnabled(isChecked);
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
                if (setting != null) {
                    Switch receivePush = findViewById(R.id.receivePush);
                    TextInputLayout selectStartTime = findViewById(R.id.selectStartTime);
                    TextInputLayout selectEndTime = findViewById(R.id.selectEndTime);
                    TextInputEditText startTime = findViewById(R.id.startTime);
                    TextInputEditText endTime = findViewById(R.id.endTime);
                    boolean doReceivePush = setting.getReceivePushNotifications();
                    String timeWindowStart = setting.getTimeWindowStart();
                    String timeWindowEnd = setting.getTimeWindowEnd();
                    selectStartTime.setEnabled(doReceivePush);
                    selectEndTime.setEnabled(doReceivePush);
                    startTime.setText(timeWindowStart);
                    endTime.setText(timeWindowEnd);
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
        TextInputEditText selectStartTime = findViewById(R.id.startTime);
        TextInputEditText selectEndTime = findViewById(R.id.endTime);
        boolean doReceivePush = receivePush.isChecked();
        String userId = prefs.getString("userId", "");
        String timeWindowStart = selectStartTime.getText().toString();
        String timeWindowEnd = selectEndTime.getText().toString();
        db.createSetting(userId, doReceivePush, timeWindowStart, timeWindowEnd);
        finish();
    }

    /*
     * Displays a Time Picker dialog to get the user to choose a start time.
     */
    public void getTime(final View v) {
        // Gets current time
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);

        // Show TimePicker dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        Calendar timeCal = new GregorianCalendar();
                        timeCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        timeCal.set(Calendar.MINUTE, minute);
                        Date time = timeCal.getTime();
                        String timePattern = "H:mm";
                        SimpleDateFormat sdf = new SimpleDateFormat(timePattern);
                        ((TextInputEditText) v).setText(sdf.format(time));
                    }
                }, hour, minute, false);
        timePickerDialog.show();
    }
}