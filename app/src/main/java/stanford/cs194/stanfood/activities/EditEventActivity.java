package stanford.cs194.stanfood.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import stanford.cs194.stanfood.R;
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
        // Get User ID to link to event
        String userId = prefs.getString("userId", "");

        // TODO: Replace with Popout Dialog with buttons for editing and deleting.
    }

    /*
     * Submits event changes/deletions to database.
     */
    public void saveChanges(View view) {
        Log.d("saveChanges", "Changes Saved!");
        finish();
    }
}
