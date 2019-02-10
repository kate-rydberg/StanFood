package stanford.cs194.stanfood;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import java.time.LocalDateTime;

public class CreateEventActivity extends AppCompatActivity {
    private Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        db = new Database();
    }

    /*
     * Creates event according to filled out information
     * Contains event name, food description, location, and event description.
     * Currently, createEvent only uses the event description and event location.
     * TODO: Add food in database once original event is created
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createEvent(View view) {
        EditText eventName = findViewById(R.id.eventName);
        EditText foodDescription = findViewById(R.id.foodDescription);
        EditText eventLocation = findViewById(R.id.eventLocation);
        EditText eventDescription = findViewById(R.id.eventDescription);
        String eventNameStr = eventName.getText().toString();
        String foodDescriptionStr = foodDescription.getText().toString();
        String eventLocationStr = eventLocation.getText().toString();
        String eventDescriptionStr = eventDescription.getText().toString();
        db.createEvent(eventDescriptionStr, eventLocationStr, LocalDateTime.now());
        finish();
    }
}
