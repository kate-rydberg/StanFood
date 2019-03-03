package stanford.cs194.stanfood.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import stanford.cs194.stanfood.R;

public class EditEventActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);
    }

    public void saveChanges(View view) {
        Log.d("saveChanges", "Changes Saved!");
    }
}
