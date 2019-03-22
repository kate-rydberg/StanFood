package stanford.cs194.stanfood.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;

import stanford.cs194.stanfood.R;
import stanford.cs194.stanfood.database.CreateList;
import stanford.cs194.stanfood.database.Database;
import stanford.cs194.stanfood.fragments.BottomSheetListView;

public class DeleteEventActivity extends AppCompatActivity {
    private Database db;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_event);
        db = new Database();
        prefs = getSharedPreferences("loginData", MODE_PRIVATE);
        getUserEventList();
    }

    @Override
    public void onResume() {
        super.onResume();
        getUserEventList();
    }

    /*
     * Refreshes the event list since deleting from the database doesn't automatically refresh the list.
     */
    public void refreshView(View view) {
        getUserEventList();
    }

    /*
     * Obtains and displays the list of all events corresponding to the logged-in user
     */
    private void getUserEventList() {
        // Get User ID to link to event
        String userId = prefs.getString("userId", "");
        Display screen = getWindowManager().getDefaultDisplay();

        FragmentManager supportFragment = getSupportFragmentManager();
        BottomSheetListView eventListView = findViewById(R.id.eventList);
        ViewGroup userEventsContent = findViewById(R.id.userEventsContent);

        CreateList eventRows = new CreateList(db, eventListView, userEventsContent, supportFragment, screen);
        eventRows.createUserEventList(userId);
    }
}
