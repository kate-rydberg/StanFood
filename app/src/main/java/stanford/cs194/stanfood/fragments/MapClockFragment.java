package stanford.cs194.stanfood.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import stanford.cs194.stanfood.R;

public class MapClockFragment extends DialogFragment {
    public static MapClockFragment newInstance() {
        MapClockFragment f = new MapClockFragment();
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.map_clock_dialog, container, false);
        return v;
    }
}
