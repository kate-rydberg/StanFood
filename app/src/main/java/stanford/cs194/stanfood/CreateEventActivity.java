package stanford.cs194.stanfood;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

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
     */
    public void createEvent(View view) {
        System.out.println("Event Created!");
    }
}
