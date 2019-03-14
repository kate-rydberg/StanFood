package stanford.cs194.stanfood.fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import stanford.cs194.stanfood.R;

public class PopUpFragment extends DialogFragment {

    private String name;
    private String location;
    private String time;
    private String description;

    public static PopUpFragment newInstance(String name, String location,
                                            String time, String description) {
        PopUpFragment p = new PopUpFragment();
        // initiate popup variables.
        p.name = name;
        p.location = location;
        p.time = time;
        p.description = description;

        return p;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View popupView = inflater.inflate(R.layout.event_popup, container, false);

        TextView infoLocationName = popupView.findViewById(R.id.infoLocationName);
        TextView infoEventTime = popupView.findViewById(R.id.infoEventTime);
        TextView infoEventDescription = popupView.findViewById(R.id.infoEventDescription);

        String locationText = infoLocationName.getText().toString() + location;
        infoLocationName.setText(locationText);
        String timeText = infoEventTime.getText().toString() + time;
        infoEventTime.setText(timeText);
        infoEventDescription.setText(description);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        popupView.setFocusable(true);
        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                dismiss();
                return true;
            }
        });

        return popupView;
    }
}
