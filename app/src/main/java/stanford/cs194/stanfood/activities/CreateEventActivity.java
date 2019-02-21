package stanford.cs194.stanfood.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;

import stanford.cs194.stanfood.App;
import stanford.cs194.stanfood.R;
import stanford.cs194.stanfood.database.Database;

import static com.google.android.libraries.places.api.Places.createClient;

public class CreateEventActivity extends AppCompatActivity {
    public static final long HOURS_TO_MS = 3600000;
    public static final long MINUTES_TO_MS = 60000;

    private Database db;
    private SharedPreferences prefs;
    private PlacesClient placesClient;
    private AutocompleteSessionToken token;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        db = new Database();
        prefs = getSharedPreferences("loginData", MODE_PRIVATE);
        token = AutocompleteSessionToken.newInstance();
        Places.initialize(getApplicationContext(),
                getResources().getString(R.string.google_maps_key));
        placesClient = createClient(this);
        AutoCompleteTextView textView = findViewById(R.id.eventAutoLocation);
        textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count > 3)
                    autocomplete(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * Extracts event name from the layout.
     * Note: Event name required
     * @return "" (empty string) if no event name given.
     */
    private String getEventName() {
        EditText eventName = findViewById(R.id.eventName);
        String eventNameStr = eventName.getText().toString().trim();

        // Error checking for required field: Event Name
        if (eventNameStr.equals("")) {
            eventName.setError("Event name required.");
        }
        return eventNameStr;
    }

    /**
     * Extracts event description from the layout.
     * Note: not required for event creation
     * @return Event description
     */
    private String getEventDescription() {
        EditText eventDescription = findViewById(R.id.eventDescription);
        return eventDescription.getText().toString().trim();
    }

    /**
     * Extracts food description from the layout
     * Note: Food description required.
     * @return "" (empty string) if no food description given.
     */
    private String getFood() {
        EditText foodDescription = findViewById(R.id.foodDescription);
        String foodDescriptionStr = foodDescription.getText().toString().trim();

        // Error checking for required field: Food Description
        if (foodDescriptionStr.equals("")) {
            foodDescription.setError("Food description required.");
        }
        return foodDescriptionStr;
    }

    /**
     * Extracts location name from the layout
     * Note: Location name required.
     * @return "" (empty string) if no location name given.
     */
    private String getLocationName() {
        EditText eventLocation = findViewById(R.id.eventLocation);
        String eventLocationStr = eventLocation.getText().toString().trim();

        // Error checking for required field: Event Location
        if (eventLocationStr.equals("")) {
            eventLocation.setError("Event Location required.");
        }
        return eventLocationStr;
    }

    /**
     * Extracts event starting date and time from the layout in the form of milliseconds
     * Note: Event Date and Start Time both required.
     * @return 0 milliseconds if event date or start time not chosen
     */
    private long getStartTimeMS() {
        TextView startDate = findViewById(R.id.startDate);
        TextView startTime = findViewById(R.id.startTime);
        String dateStr = startDate.getText().toString().trim();
        String timeStr = startTime.getText().toString().trim();

        // Error checking for required fields: Start time, Start date
        boolean missingStartTime = false;
        if (dateStr.equals("")) {
            startDate.requestFocus();
            startDate.setError("Event Date required.");
            missingStartTime = true;
        } else {
            startDate.setError(null);
        }
        if (timeStr.equals("")) {
            startTime.requestFocus();
            startTime.setError("Start Time required.");
            missingStartTime = true;
        } else {
            startTime.setError(null);
        }
        if (missingStartTime) {
            return 0;
        }

        // Convert inputted date and time to milliseconds
        String[] dates = dateStr.split("-");
        String[] times = timeStr.split(":");
        Calendar cal;

        // Fills Calendar with inputted date and time if present, else current time
        if (dates.length == 3 && times.length == 2) {
            int year = Integer.parseInt(dates[0]);
            int month = Integer.parseInt(dates[1]);
            int day = Integer.parseInt(dates[2]);
            int hour = Integer.parseInt(times[0]);
            int minute = Integer.parseInt(times[1]);
            cal = new GregorianCalendar(year, month - 1, day, hour, minute);
        } else {
            cal = Calendar.getInstance();
        }
        return cal.getTimeInMillis();
    }

    /**
     * Extracts event duration in milliseconds from the layout.
     * Note: Some non-zero duration required, but hours and minutes don't both have to be filled.
     * @return 0 if invalid or empty duration.
     */
    private long getDurationMS(){
        EditText hoursDuration = findViewById(R.id.hours);
        EditText minutesDuration = findViewById(R.id.minutes);
        String hoursStr = hoursDuration.getText().toString().trim();
        String minutesStr = minutesDuration.getText().toString().trim();

        long hoursLong = 0;
        long minutesLong = 0;
        if (!hoursStr.equals("")) {
            hoursLong = Long.parseLong(hoursStr);
        }
        if (!minutesStr.equals("")) {
            minutesLong = Long.parseLong(minutesStr);
        }
        // Converts given hours and minutes to milliseconds
        long durationMS = hoursLong * HOURS_TO_MS + minutesLong * MINUTES_TO_MS;

        // Error checking for required field: Duration
        if (durationMS == 0) {
            hoursDuration.setError("Non-zero duration needed.");
            minutesDuration.setError("Non-zero duration needed.");
        } else {
            hoursDuration.setError(null);
            minutesDuration.setError(null);
        }
        return durationMS;
    }

    /**
     * Creates event according to filled out information
     * Contains event name, food description, location name, event description,
     * start date and time, and duration.
     */
    public void createEvent(View view) {
        String eventName = getEventName();
        String eventDescription = getEventDescription();
        String foodDescription = getFood();
        String locationName = getLocationName();
        long startTimeMS = getStartTimeMS();
        long durationMS = getDurationMS();

        // Get User ID to link to event
        String userId = prefs.getString("userId", "");

        // If any fields empty/invalid, return without attempting database event creation
        if (eventName.equals("") || foodDescription.equals("") || locationName.equals("")
            || startTimeMS == 0 || durationMS == 0 || userId.equals("")) {
            return;
        }

        db.createEvent(eventName, eventDescription, locationName, startTimeMS, durationMS, foodDescription, userId);

        Context context = getApplicationContext();
        String text = "Event creation successful!";
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        final int BOTTOM_SHEET_PEEK_HEIGHT = (int)context.getResources().getDimension(R.dimen.bottom_sheet_peek_height);
        toast.setGravity(Gravity.BOTTOM, 0, BOTTOM_SHEET_PEEK_HEIGHT);
        toast.show();
        finish();
    }

    /*
     * Displays a Date Picker dialog to get the user to choose a start date.
     */
    public void getDate(View v) {
        final TextView dateText = findViewById(R.id.startDate);

        // Gets current date
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        // Show DatePicker dialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
            new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    Calendar dateCal = new GregorianCalendar(year, monthOfYear, dayOfMonth);
                    Date date = dateCal.getTime();
                    String datePattern = "yyyy-MM-dd";
                    SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
                    dateText.setText(sdf.format(date));
                }
            }, year, month, day);
        datePickerDialog.show();
    }

    /*
     * Displays a Time Picker dialog to get the user to choose a start time.
     */
    public void getTime(View v) {
        final TextView timeText = findViewById(R.id.startTime);

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
                    timeText.setText(sdf.format(time));
                }
            }, hour, minute, false);
        timePickerDialog.show();
    }

    public void autocomplete(String query){
        RectangularBounds bounds = RectangularBounds.newInstance(
                new LatLng(37.418814, -122.159055),
                new LatLng(37.449061, -122.192363)
        );
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                // Call either setLocationBias() OR setLocationRestriction().
                //.setLocationBias(bounds)
                .setLocationRestriction(bounds)
                .setTypeFilter(TypeFilter.GEOCODE)
                .setSessionToken(token)
                .setQuery(query)
                .build();

        final ArrayList<String> suggestions = new ArrayList<String>();
        placesClient.findAutocompletePredictions(request).addOnSuccessListener(
                new OnSuccessListener<FindAutocompletePredictionsResponse>() {
            @Override
            public void onSuccess(FindAutocompletePredictionsResponse response) {
                for(AutocompletePrediction prediction : response.getAutocompletePredictions()){
                    suggestions.add(prediction.getPrimaryText(null).toString());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("CONSOLE", e.toString());
            }
        });
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, suggestions);
        AutoCompleteTextView actv = (AutoCompleteTextView) findViewById(R.id.eventAutoLocation);
        actv.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
        actv.setTextColor(Color.RED);
    }
}