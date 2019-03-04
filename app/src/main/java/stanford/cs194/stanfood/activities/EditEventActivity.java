package stanford.cs194.stanfood.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import stanford.cs194.stanfood.R;
import stanford.cs194.stanfood.database.CreateList;
import stanford.cs194.stanfood.database.Database;

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
        CreateList initRows = new CreateList(db);
        // Get User ID to link to event
        String userId = prefs.getString("userId", "");
        ListView userEventList = findViewById(R.id.userEvents);
        ViewCompat.setNestedScrollingEnabled(userEventList, true);
        initRows.createUserEventList(userId, userEventList);
    }

    /*
     * Submits event changes/deletions to database.
     */
    public void saveChanges(View view) {
        Log.d("saveChanges", "Changes Saved!");
    }
}
