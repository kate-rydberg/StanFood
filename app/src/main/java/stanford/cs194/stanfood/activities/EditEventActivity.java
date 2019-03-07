package stanford.cs194.stanfood.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import stanford.cs194.stanfood.App;
import stanford.cs194.stanfood.R;
import stanford.cs194.stanfood.database.CreateList;
import stanford.cs194.stanfood.database.Database;
import stanford.cs194.stanfood.fragments.BottomSheetListView;
import stanford.cs194.stanfood.helpers.ViewGroupUtils;

public class EditEventActivity extends AppCompatActivity {
    private Database db;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);
        db = new Database();
        prefs = getSharedPreferences("loginData", MODE_PRIVATE);
        getUserEventList();
    }

    /*
     * Obtains the list of all events corresponding to the logged-in user
     */
    private void getUserEventList() {
        // Get User ID to link to event
        String userId = prefs.getString("userId", "");
        BottomSheetListView eventListView = findViewById(R.id.eventList);
        ViewGroup bottomSheetContents = findViewById(R.id.userEventsContent);
        if (eventListView == null) {
            LayoutInflater viewInflater = LayoutInflater.from(App.getContext());
            eventListView = (BottomSheetListView) viewInflater.inflate(R.layout.list_info, null, true);
            ViewGroupUtils.replaceViewById(eventListView, bottomSheetContents, 0);
        }
        ViewCompat.setNestedScrollingEnabled(eventListView, true);

        CreateList initRows = new CreateList(db, eventListView, bottomSheetContents);
        initRows.createUserEventList(userId);
    }

    /*
     * Submits event changes/deletions to database.
     */
    public void saveChanges(View view) {
        Log.d("saveChanges", "Changes Saved!");
    }
}
