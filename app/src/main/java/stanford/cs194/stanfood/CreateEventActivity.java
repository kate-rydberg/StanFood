package stanford.cs194.stanfood;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.TimeZone;

public class CreateEventActivity extends AppCompatActivity {
    public static final long HOURS_TO_MS = 3600000;
    public static final long MINUTES_TO_MS = 60000;

    private Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        db = new Database();
    }

    /**
     * Creates event according to filled out information
     * Contains event name, food description, location name, event description,
     * start date and time, and duration.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createEvent(View view) {
        // Extract event and food information
        EditText eventName = findViewById(R.id.eventName);
        EditText foodDescription = findViewById(R.id.foodDescription);
        EditText eventDescription = findViewById(R.id.eventDescription);
        String eventNameStr = eventName.getText().toString();
        String foodDescriptionStr = foodDescription.getText().toString();
        String eventDescriptionStr = eventDescription.getText().toString();
        String completeEventStr = eventNameStr + "\n" + eventDescriptionStr;

        // Get given location name
        EditText eventLocation = findViewById(R.id.eventLocation);
        String eventLocationStr = eventLocation.getText().toString();

        // Process current time zone and convert inputted date and time to milliseconds
        EditText startDate = findViewById(R.id.startDate);
        EditText startTime = findViewById(R.id.startTime);
        String dateStr = startDate.getText().toString();
        String timeStr = startTime.getText().toString();
        String[] dates = dateStr.split("-");
        String[] times = timeStr.split(":");
        LocalDateTime ldt;

        // Fills LocalDateTime with inputted date and time if present, else current time
        if (dates.length == 3 && times.length == 2) {
            int day = Integer.parseInt(dates[0]);
            int month = Integer.parseInt(dates[1]);
            int year = Integer.parseInt(dates[2]);
            int hour = Integer.parseInt(times[0]);
            int minute = Integer.parseInt(times[1]);
            ldt = LocalDateTime.of(year, month, day, hour, minute);
        } else {
            ldt = LocalDateTime.now();
        }
        ZoneId timeZone = TimeZone.getDefault().toZoneId();
        ZonedDateTime zdt = ldt.atZone(timeZone);
        long ms = zdt.toInstant().toEpochMilli();

        // Process given duration by converting hours and minutes to milliseconds
        EditText hoursDuration = findViewById(R.id.hours);
        EditText minutesDuration = findViewById(R.id.minutes);
        long hoursLong = Long.parseLong(hoursDuration.getText().toString());
        long minutesLong = Long.parseLong(minutesDuration.getText().toString());
        long durationMs = hoursLong * HOURS_TO_MS + minutesLong * MINUTES_TO_MS;

        db.createEvent(completeEventStr, eventLocationStr, ldt, foodDescriptionStr);
        //db.createEvent(completeEventStr, eventLocationStr, ms, foodDescriptionStr);

        finish();
    }

    /*
     * Displays a Date Picker dialog to get the user to choose a start date.
     */
    public void getDate(View v) {
        final EditText dateText = findViewById(R.id.startDate);

        // Gets current date
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Show DatePicker dialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
            new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    dateText.setText(getResources().getString(R.string.date_format,
                            dayOfMonth, monthOfYear + 1, year));
                }
            }, year, month, day);
        datePickerDialog.show();
    }

    /*
     * Displays a Time Picker dialog to get the user to choose a start time.
     */
    public void getTime(View v) {
        final EditText timeText = findViewById(R.id.startTime);

        // Gets current time
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Show TimePicker dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
            new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    timeText.setText(getResources().getString(R.string.time_format,
                            hourOfDay, minute));
                }
            }, hour, minute, false);
        timePickerDialog.show();
    }
}